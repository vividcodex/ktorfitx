package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.expends.isWSOrWSS
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.ClassModel
import cn.ktorfitx.multiplatform.ksp.model.FunModel
import cn.ktorfitx.multiplatform.ksp.model.TimeoutModel
import cn.ktorfitx.multiplatform.ksp.model.WebSocketModel
import com.squareup.kotlinpoet.CodeBlock

internal class WebSocketCodeBuilder(
	private val classModel: ClassModel,
	private val funModel: FunModel,
	private val webSocketModel: WebSocketModel,
	private val tokenVarName: String?
) {
	
	private val parameterModels = funModel.parameterModels
	
	fun CodeBlock.Builder.buildCodeBlock() {
		fileSpecBuilder.addImport(PackageNames.KTOR_WEBSOCKET, "webSocket")
		addStatement("this.config.httpClient.webSocket(")
		indent()
		addStatement("urlString = %S,", parseToFullUrl())
		buildRequestCodeBlock(
			buildBearerAuth = { buildBearerAuth(it) },
			buildTimeout = { buildTimeout(it) }
		)
		buildBlock()
		unindent()
		addStatement(")")
	}
	
	private fun parseToFullUrl(): String {
		val url = webSocketModel.url
		if (url.isWSOrWSS()) return url
		val apiUrl = classModel.apiUrl
		if (apiUrl == null) return url
		return "$apiUrl/$url"
	}
	
	private fun CodeBlock.Builder.buildRequestCodeBlock(
		buildBearerAuth: CodeBlock.Builder.(String) -> Unit,
		buildTimeout: CodeBlock.Builder.(timeoutModel: TimeoutModel) -> Unit
	) {
		val timeoutModel = funModel.timeoutModel
		if (tokenVarName == null && timeoutModel == null) return
		beginControlFlow("request = ")
		tokenVarName?.let { buildBearerAuth(it) }
		timeoutModel?.let { buildTimeout(it) }
		endControlFlow()
		add(",")
	}
	
	private fun CodeBlock.Builder.buildBearerAuth(
		tokenVarName: String
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "bearerAuth")
		beginControlFlow("if (%N != null)", tokenVarName)
		addStatement("this.bearerAuth(%N)", tokenVarName)
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildTimeout(
		timeoutModel: TimeoutModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_PLUGINS, "timeout")
		beginControlFlow("this.timeout")
		if (timeoutModel.requestTimeoutMillis != null) {
			addStatement("this.requestTimeoutMillis = %LL", timeoutModel.requestTimeoutMillis)
		}
		if (timeoutModel.connectTimeoutMillis != null) {
			addStatement("this.connectTimeoutMillis = %LL", timeoutModel.connectTimeoutMillis)
		}
		if (timeoutModel.socketTimeoutMillis != null) {
			addStatement("this.socketTimeoutMillis = %LL", timeoutModel.socketTimeoutMillis)
		}
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildBlock() {
		val varName = parameterModels.first().varName
		addStatement("block = %N", varName)
	}
}