package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktor.client.ksp.model.EncryptInfo
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.*
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asTypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/4 下午11:22
 *
 * 介绍：KtorCodeBlock
 */
internal class KtorCodeBlock(
	addImport: (String, Array<out String>) -> Unit
) : BuildCodeBlock(addImport) {
	
	private companion object {
		
		private val byteArrayTypeName by lazy { ByteArray::class.asTypeName() }
		
		private val resultBodyTypeName by lazy { ResultBody::class.asTypeName() }
		
		private val unitTypeName by lazy { Unit::class.asTypeName() }
		
		private val pathRegex = ".*\\{.*\\}.*".toRegex()
	}
	
	/**
	 * 构建 Ktor 网络请求代码
	 */
	fun CodeBlock.Builder.buildKtorCodeBlock(
		classStructure: ClassStructure,
		funStructure: FunStructure
	) {
		val needReturn = funStructure.returnStructure.typeName != Unit::class.asTypeName()
		beginControlFlow(if (needReturn) "return try" else "try")
		val functionModels = funStructure.functionModels
		val apiModel = functionModels.filterIsInstance<ApiModel>().first()
		val requestType = apiModel.requestType.toString().lowercase()
		addImport("io.ktor.client.request", requestType)
		val apiUrl = classStructure.apiStructure.url
		val pathModels = funStructure.valueParameterModels.filterIsInstance<PathModel>()
		val funName = funStructure.funName
		val url = parsePathToUrl(apiUrl, apiModel.url, pathModels, funName)
		val valueParameterModels = funStructure.valueParameterModels
		val httpClientCode = "${if (needReturn) "val response = " else ""}this.httpClient.$requestType(\"\${this.ktorConfig.baseUrl}$url\")"
		val needHttpRequestBuilder = needHttpRequestBuilder(funStructure, apiModel.auth)
		if (needHttpRequestBuilder) {
			beginControlFlow(httpClientCode)
		} else {
			addStatement(httpClientCode)
		}
		if (apiModel.auth) {
			buildAuthCodeBlock()
		}
		valueParameterModels.filterIsInstance<BodyModel>().firstOrNull()?.let {
			buildBodyCodeBlock(it)
		}
		valueParameterModels.filterIsInstance<QueryModel>().let {
			if (it.isNotEmpty()) {
				buildQueryCodeBlock(it)
			}
		}
		valueParameterModels.filterIsInstance<FormModel>().let {
			if (it.isNotEmpty()) {
				buildFormCodeBlock(it)
			}
		}
		valueParameterModels.filterIsInstance<HeaderModel>().let {
			if (it.isNotEmpty()) {
				buildHeaderCodeBlock(it)
			}
		}
		functionModels.filterIsInstance<HeadersModel>().firstOrNull()?.let {
			buildHeadersCodeBlock(it)
		}
		if (needHttpRequestBuilder) {
			endControlFlow()
		}
		val catchModels = valueParameterModels.filterIsInstance<CatchModel>()
		val finallyModels = valueParameterModels.filterIsInstance<FinallyModel>()
		when (funStructure.returnStructure.rawType) {
			byteArrayTypeName -> buildReturnByteArrayCodeBlock(catchModels, finallyModels)
			resultBodyTypeName -> buildReturnResultBodyCodeBlock(catchModels, finallyModels)
			unitTypeName -> buildReturnUnitCodeBlock(catchModels, finallyModels)
			else -> error("不支持的类型")
		}
	}
	
	private fun needHttpRequestBuilder(funStructure: FunStructure, auth: Boolean): Boolean {
		return auth ||
			funStructure.valueParameterModels.any { it is BodyModel || it is QueryModel || it is FormModel || it is HeaderModel } ||
			funStructure.functionModels.any { it is HeadersModel }
	}
	
	/**
	 * 解析 Path
	 */
	private fun parsePathToUrl(apiUrl: String, url: String, pathModels: List<PathModel>, funName: String): String {
		var fullUrl = apiUrl + url
		pathModels.forEach {
			check(it.name.isNotBlank()) {
				"$funName 方法的 ${it.varName} 参数上的 @Path 注解的 name 不能为空"
			}
			check(fullUrl.contains("{${it.name}}")) {
				"$funName 方法的 ${it.varName} 参数上的 @Path 注解的 name 未在 url 上找到"
			}
			val newValue = "\${${formatVarName(it.varName, it.encryptInfo)}}"
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
	 * 构建 Auth
	 */
	private fun CodeBlock.Builder.buildAuthCodeBlock() {
		addImport("io.ktor.client.request", "bearerAuth")
		beginControlFlow("ktorConfig.token?.let")
		addStatement("bearerAuth(it())")
		endControlFlow()
	}
	
	/**
	 * 构建 Body
	 */
	private fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel) {
		addImport("kotlinx.serialization", "encodeToString")
		addImport("kotlinx.serialization.json", "Json")
		addImport("io.ktor.http", "contentType", "ContentType")
		addImport("io.ktor.http.content", "TextContent")
		addImport("io.ktor.client.request.setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(TextContent(Json.encodeToString(${bodyModel.varName}), ContentType.Application.Json))")
	}
	
	/**
	 * 构建 Form
	 */
	private fun CodeBlock.Builder.buildFormCodeBlock(formModel: List<FormModel>) {
		addImport("io.ktor.http", "contentType", "ContentType")
		addImport("io.ktor.client.request", "setBody")
		addImport("io.ktor.client.request.forms", "formData", "MultiPartFormDataContent")
		addStatement("contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("val formData = formData {")
		formModel.forEach {
			val varName = formatVarName(it.varName, it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
		addStatement("setBody(MultiPartFormDataContent(formData))")
	}
	
	/**
	 * 构建 Header
	 */
	private fun CodeBlock.Builder.buildHeaderCodeBlock(headerModels: List<HeaderModel>) {
		addImport("io.ktor.client.request", "header")
		headerModels.forEach {
			val varName = formatVarName(it.varName, it.encryptInfo)
			addStatement("header(\"${it.name}\", $varName)")
		}
	}
	
	/**
	 * 构建 Headers
	 */
	private fun CodeBlock.Builder.buildHeadersCodeBlock(headersModel: HeadersModel) {
		addImport("io.ktor.client.request", "header")
		headersModel.headerMap.forEach { (key, value) ->
			addStatement("header(\"$key\", \"$value\")")
		}
	}
	
	/**
	 * 构建 Query
	 */
	private fun CodeBlock.Builder.buildQueryCodeBlock(queryModels: List<QueryModel>) {
		addImport("io.ktor.client.request", "parameter")
		queryModels.forEach {
			val varName = formatVarName(it.varName, it.encryptInfo)
			addStatement("parameter(\"${it.name}\", $varName)")
		}
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
		addImport("cn.vividcode.multiplatform.ktor.client.api.model", "ResultBody")
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
	 * 解析变量名
	 */
	private fun formatVarName(varName: String, encryptInfo: EncryptInfo?): String {
		if (encryptInfo == null) return varName
		addImport("cn.vividcode.multiplatform.ktor.client.api.encrypt", "encrypt", "EncryptType")
		return "$varName.encrypt(EncryptType.${encryptInfo.encryptType}, ${encryptInfo.layer})"
	}
}