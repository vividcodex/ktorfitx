package cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfit.ksp.expends.rawType
import cn.vividcode.multiplatform.ktorfit.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktorfit.ksp.model.EncryptInfo
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.*
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/4 23:22
 *
 * 文件介绍：HttpClientCodeBlock
 */
internal class HttpClientCodeBlockBuilder(
	private val classStructure: ClassStructure,
	private val funStructure: FunStructure,
	addImport: (String, Array<out String>) -> Unit
) : CodeBlockBuilder(funStructure, addImport) {
	
	private companion object {
		
		private val byteArrayClassName by lazy { ByteArray::class.asClassName() }
		
		private val resultBodyClassName by lazy { ClassName("cn.vividcode.multiplatform.ktorfit.api.model", "ResultBody") }
		
		private val unitClassName by lazy { Unit::class.asClassName() }
	}
	
	private val functionModels by lazy { funStructure.functionModels }
	
	private val valueParameterModels by lazy { funStructure.valueParameterModels }
	
	private val returnStructure = funStructure.returnStructure
	
	private val responseVarName by lazy { getVarName("response") }
	
	/**
	 * 构建 KtorCodeBlock
	 */
	override fun CodeBlock.Builder.buildCodeBlock() {
		val needReturn = returnStructure.typeName != Unit::class.asTypeName()
		beginControlFlow(if (needReturn) "return try" else "try")
		val apiModel = functionModels.filterIsInstance<ApiModel>().first()
		val funName = apiModel.requestFunName
		addImport("io.ktor.client.request", funName)
		val url = parsePathToUrl(apiModel.url)
		val httpClientCode = "${if (needReturn) "val $responseVarName = " else ""}this.httpClient.$funName(\"\${this.ktorfitConfig.baseUrl}$url\")"
		val needHttpRequestBuilder = isNeedHttpRequestBuilder(apiModel.auth)
		if (needHttpRequestBuilder) {
			beginControlFlow(httpClientCode)
		} else {
			addStatement(httpClientCode)
		}
		buildBearerAuthCodeBlock(apiModel.auth)
		buildHeadersCodeBlock()
		buildQueryCodeBlock()
		buildFormCodeBlock()
		buildBodyCodeBlock()
		if (needHttpRequestBuilder) {
			endControlFlow()
		}
		val catchModels = valueParameterModels.filterIsInstance<CatchModel>()
		val finallyModels = valueParameterModels.filterIsInstance<FinallyModel>()
		when (returnStructure.typeName.rawType) {
			byteArrayClassName -> buildReturnByteArrayCodeBlock(catchModels, finallyModels)
			resultBodyClassName -> buildReturnResultBodyCodeBlock(catchModels, finallyModels)
			unitClassName -> buildReturnUnitCodeBlock(catchModels, finallyModels)
			else -> error("不支持的类型")
		}
	}
	
	private fun isNeedHttpRequestBuilder(auth: Boolean): Boolean {
		return auth || valueParameterModels.any {
			it is BodyModel || it is QueryModel || it is FormModel || it is HeaderModel
		} || functionModels.any { it is HeadersModel }
	}
	
	/**
	 * 解析 Path
	 */
	private fun parsePathToUrl(url: String): String {
		val funName = funStructure.funName
		var fullUrl = classStructure.apiStructure.url + url
		val pathModels = valueParameterModels.filterIsInstance<PathModel>()
		pathModels.forEach {
			check(it.name.isNotBlank()) {
				"$funName 方法的 ${it.varName} 参数上的 @Path 注解的 name 不能为空"
			}
			check(fullUrl.contains("{${it.name}}")) {
				"$funName 方法的 ${it.varName} 参数上的 @Path 注解的 name 未在 url 上找到"
			}
			val newValue = "\${${it.varName}${encrypt(it.encryptInfo)}}"
			fullUrl = fullUrl.replace("{${it.name}}", newValue)
		}
		val notFoundPath = getNotFoundPath(fullUrl)
		check(notFoundPath == null) {
			"$funName 方法的 url 参数上名称为 $notFoundPath 的 path 没有找到"
		}
		return fullUrl
	}
	
	/**
	 * 获取没有找到的Path
	 */
	private fun getNotFoundPath(fullUrl: String): String? {
		for (i in 1 ..< fullUrl.length) {
			if (fullUrl[i] == '{' && fullUrl[i - 1] != '$') {
				val j = fullUrl.indexOf('}', i)
				return fullUrl.substring(i + 1, j)
			}
		}
		return null
	}
	
	/**
	 * 构建 bearerAUth
	 */
	private fun CodeBlock.Builder.buildBearerAuthCodeBlock(auth: Boolean) {
		if (auth) {
			addImport("io.ktor.client.request", "bearerAuth")
			addStatement("bearerAuth(ktorfitConfig.token!!())")
		}
	}
	
	/**
	 * 构建 headers
	 */
	private fun CodeBlock.Builder.buildHeadersCodeBlock() {
		val headersModel = functionModels.filterIsInstance<HeadersModel>().firstOrNull()
		val headerModels = valueParameterModels.filterIsInstance<HeaderModel>()
		if (headersModel == null && headerModels.isEmpty()) return
		addImport("io.ktor.client.request", "headers")
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(\"$name\", \"$value\")")
		}
		headerModels.forEach {
			val varName = it.varName + encrypt(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	/**
	 * 构建 Query
	 */
	private fun CodeBlock.Builder.buildQueryCodeBlock() {
		val queryModels = valueParameterModels.filterIsInstance<QueryModel>()
		if (queryModels.isEmpty()) return
		addImport("io.ktor.client.request", "parameter")
		queryModels.forEach {
			val varName = it.varName + encrypt(it.encryptInfo)
			addStatement("parameter(\"${it.name}\", $varName)")
		}
	}
	
	/**
	 * 构建 Form
	 */
	private fun CodeBlock.Builder.buildFormCodeBlock() {
		val formModels = valueParameterModels.filterIsInstance<FormModel>()
		if (formModels.isEmpty()) return
		addImport("io.ktor.http", "contentType", "ContentType")
		addImport("io.ktor.client.request", "setBody")
		addImport("io.ktor.client.request.forms", "formData", "MultiPartFormDataContent")
		addStatement("contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("val formData = formData {")
		formModels.forEach {
			val varName = it.varName + encrypt(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
		addStatement("setBody(MultiPartFormDataContent(formData))")
	}
	
	/**
	 * 构建 Body
	 */
	private fun CodeBlock.Builder.buildBodyCodeBlock() {
		val bodyModel = valueParameterModels.filterIsInstance<BodyModel>().firstOrNull() ?: return
		addImport("io.ktor.http", "contentType", "ContentType")
		addImport("io.ktor.client.request", "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(${bodyModel.varName})")
	}
	
	/**
	 * 构建 ByteArray
	 */
	private fun CodeBlock.Builder.buildReturnByteArrayCodeBlock(
		catchModels: List<CatchModel>,
		finallyModels: List<FinallyModel>
	) {
		addImport("io.ktor.http", "isSuccess")
		addImport("io.ktor.client.statement", "readBytes")
		beginControlFlow("if (response.status.isSuccess())")
		addStatement("response.readBytes()")
		nextControlFlow("else")
		addStatement("ByteArray(0)")
		endControlFlow()
		buildExceptionCodeBlock(catchModels, finallyModels) {
			addStatement("ByteArray(0)")
		}
	}
	
	/**
	 * 构建 ResultBody
	 */
	private fun CodeBlock.Builder.buildReturnResultBodyCodeBlock(
		catchModels: List<CatchModel>,
		finallyModels: List<FinallyModel>
	) {
		addImport("cn.vividcode.multiplatform.ktorfit.api.model", "ResultBody")
		addImport("io.ktor.http", "isSuccess")
		addImport("io.ktor.client.call", "body")
		beginControlFlow("if (response.status.isSuccess())")
		addStatement("response.body()")
		nextControlFlow("else")
		addStatement("ResultBody.failure(response.status.value, response.status.description)")
		endControlFlow()
		buildExceptionCodeBlock(catchModels, finallyModels) {
			addStatement("ResultBody.exception(e)")
		}
	}
	
	/**
	 * 构建 Unit
	 */
	private fun CodeBlock.Builder.buildReturnUnitCodeBlock(
		catchModels: List<CatchModel>,
		finallyModels: List<FinallyModel>
	) {
		buildExceptionCodeBlock(catchModels, finallyModels)
	}
	
	/**
	 * 构建 异常处理回调
	 */
	private fun CodeBlock.Builder.buildExceptionCodeBlock(
		catchModels: List<CatchModel>,
		finallyModels: List<FinallyModel>,
		returnCodeBlock: (CodeBlock.Builder.() -> Unit)? = null
	) {
		val catchModelsMap = catchModels.groupBy { it.exceptionTypeName }
		if (catchModelsMap.isEmpty() && returnCodeBlock != null) {
			nextControlFlow("catch (e: Exception)")
			returnCodeBlock()
		}
		catchModelsMap.forEach { (exception, models) ->
			addImport(exception)
			nextControlFlow("catch (e: ${exception.simpleName})")
			models.forEach {
				addStatement("${it.varName}.run(e)")
			}
			returnCodeBlock?.invoke(this)
		}
		if ((catchModelsMap.isEmpty() && returnCodeBlock == null) || finallyModels.isNotEmpty()) {
			nextControlFlow("finally")
		}
		finallyModels.forEach {
			addStatement("${it.varName}.run()")
		}
		endControlFlow()
	}
	
	/**
	 * encrypt
	 */
	private fun encrypt(encryptInfo: EncryptInfo?): String {
		if (encryptInfo == null) return ""
		addImport("cn.vividcode.multiplatform.ktorfit.annotation", "EncryptType")
		addImport("cn.vividcode.multiplatform.ktorfit.api.encrypt", "encrypt")
		return ".encrypt(EncryptType.${encryptInfo.encryptType}, ${encryptInfo.layer})"
	}
}