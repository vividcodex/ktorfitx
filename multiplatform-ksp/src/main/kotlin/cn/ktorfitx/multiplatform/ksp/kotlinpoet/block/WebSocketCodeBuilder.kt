package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.expends.isWSOrWSS
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.ApiModel
import cn.ktorfitx.multiplatform.ksp.model.model.TimeoutModel
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock

internal class WebSocketCodeBuilder(
	classStructure: ClassStructure,
	funStructure: FunStructure,
	private val tokenVarName: String?
) {
	
	private val apiStructure = classStructure.apiStructure
	private val funModels = funStructure.funModels
	private val parameterModels = funStructure.parameterModels
	
	fun CodeBlock.Builder.buildCodeBlock() {
		fileSpecBuilder.addImport(PackageNames.KTOR_WEBSOCKET, "webSocket")
		addStatement("this.config.httpClient!!.webSocket(")
		indent()
		addStatement("urlString = %S,", parseToFullUrl())
		buildRequestParam(
			buildBearerAuth = { buildBearerAuth(it) },
			buildTimeout = { buildTimeout(it) }
		)
		buildBlock()
		unindent()
		addStatement(")")
	}
	
	private fun parseToFullUrl(): String {
		val url = funModels.filterIsInstance<ApiModel>().first().url
		if (url.isWSOrWSS()) return url
		val apiUrl = apiStructure.url
		if (apiUrl == null) return url
		return "$apiUrl/$url"
	}
	
	private fun CodeBlock.Builder.buildRequestParam(
		buildBearerAuth: CodeBlock.Builder.(String) -> Unit,
		buildTimeout: CodeBlock.Builder.(timeoutModel: TimeoutModel) -> Unit
	) {
		val timeoutModel = funModels.filterIsInstance<TimeoutModel>().firstOrNull()
		if (tokenVarName == null && timeoutModel == null) return
		beginControlFlow("request = ")
		if (tokenVarName != null) {
			buildBearerAuth(tokenVarName)
		}
		if (timeoutModel != null) {
			buildTimeout(timeoutModel)
		}
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