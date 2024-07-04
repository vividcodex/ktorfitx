package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.code

import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
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
	
	/**
	 * 构建 Ktor 网络请求代码
	 */
	fun CodeBlock.Builder.buildKtorCodeBlock(
		classStructure: ClassStructure,
		funStructure: FunStructure
	) {
		val needReturn = funStructure.returnStructure.typeName != Unit::class.asTypeName()
		beginControlFlow(if (needReturn) "return try" else "try")
		val functions = funStructure.functionModels
		val valueParameters = funStructure.valueParameterModels
		val apiModel = functions.filterIsInstance<ApiModel>().first()
		val response = if (needReturn) "val response = " else ""
		val requestType = apiModel.requestType.toString().lowercase()
		addImport("io.ktor.client.request", requestType)
		val pathModels = functions.filterIsInstance<PathModel>()
		val url = parsePathToUrl(classStructure.apiStructure.url, apiModel.url, pathModels)
		beginControlFlow("${response}this.httpClient.$requestType(\"\${this.ktorConfig.baseUrl}$url\")")
		if (apiModel.auth) {
			buildAuthCodeBlock()
		}
		valueParameters.filterIsInstance<BodyModel>().firstOrNull()?.let {
			buildBodyCodeBlock(it)
		}
		valueParameters.filterIsInstance<QueryModel>().ifNotEmpty {
			buildQueryCodeBlock(it)
		}
		valueParameters.filterIsInstance<FormModel>().ifNotEmpty {
			buildFormCodeBlock(it)
		}
		valueParameters.filterIsInstance<HeaderModel>().ifNotEmpty {
			buildHeaderCodeBlock(it)
		}
		functions.filterIsInstance<HeadersModel>().firstOrNull()?.let {
			buildHeadersCodeBlock(it)
		}
		endControlFlow()
		when (funStructure.returnStructure.className) {
			ByteArray::class.asClassName() -> buildReturnByteArrayCodeBlock()
			ResultBody::class.asClassName() -> buildReturnResultBodyCodeBlock()
			Unit::class.asClassName() -> buildReturnUnitCodeBlock()
		}
		endControlFlow()
	}
	
	/**
	 * 解析 Path
	 */
	private fun parsePathToUrl(apiUrl: String, url: String, pathModels: List<PathModel>): String {
		var fullUrl = apiUrl + url
		pathModels.forEach { pathModel ->
			val newValue = formatVarName(pathModel.varName, pathModel.encryptInfo)
			fullUrl = fullUrl.replace("{${pathModel.name}}", newValue)
		}
		return fullUrl
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
			addStatement("header(\"${it.name}\", $varName")
		}
	}
	
	/**
	 * 构建 Headers
	 */
	private fun CodeBlock.Builder.buildHeadersCodeBlock(headersModel: HeadersModel) {
		addImport("io.ktor.client.request", "header")
		headersModel.headerMap.forEach { (key, value) ->
			addStatement("header(\"$key\", \"$value\"")
		}
	}
	
	/**
	 * 构建 Query
	 */
	private fun CodeBlock.Builder.buildQueryCodeBlock(queryModels: List<QueryModel>) {
		addImport("io.ktor.client.request", "parameter")
		queryModels.forEach {
			val varName = formatVarName(it.varName, it.encryptInfo)
			addStatement("parameter(\"${it.name}\", $varName")
		}
	}
	
	/**
	 * 构建 ByteArray
	 */
	private fun CodeBlock.Builder.buildReturnByteArrayCodeBlock() {
		addImport("io.ktor.http", "isSuccess")
		addImport("io.ktor.client.statement", "readBytes")
		beginControlFlow("if (response.status.isSuccess())")
		addStatement("response.readBytes()")
		nextControlFlow("else")
		addStatement("ByteArray(0)")
		endControlFlow()
		nextControlFlow("catch (e: Exception)")
		addStatement("ByteArray(0)")
	}
	
	/**
	 * 构建 ResultBody
	 */
	private fun CodeBlock.Builder.buildReturnResultBodyCodeBlock() {
		addImport("cn.vividcode.multiplatform.ktor.client.api.model", "ResultBody")
		addImport("io.ktor.http", "isSuccess")
		addImport("io.ktor.client.call", "body")
		beginControlFlow("if (response.status.isSuccess())")
		addStatement("response.body()")
		nextControlFlow("else")
		addStatement("ResultBody.failure(response.status.value, response.status.description)")
		endControlFlow()
		nextControlFlow("catch (e: Exception)")
		addStatement("ResultBody.exception(e)")
	}
	
	/**
	 * 构建 Unit
	 */
	private fun CodeBlock.Builder.buildReturnUnitCodeBlock() {
		nextControlFlow("finally")
	}
	
	/**
	 * if not empty
	 */
	private fun <T : Any> List<T>.ifNotEmpty(block: (List<T>) -> Unit) {
		if (this.isNotEmpty()) {
			block(this)
		}
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