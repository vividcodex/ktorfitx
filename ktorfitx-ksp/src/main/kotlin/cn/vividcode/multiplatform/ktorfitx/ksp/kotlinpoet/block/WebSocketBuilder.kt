package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isWSOrWSS
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.UseImports
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ApiModel
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.BearerAuthModel
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock

internal class WebSocketBuilder(
	classStructure: ClassStructure,
	funStructure: FunStructure
) {
	
	private val className = classStructure.className
	private val apiStructure = classStructure.apiStructure
	private val parameterModels = funStructure.parameterModels
	private val functionModels = funStructure.functionModels
	
	fun CodeBlock.Builder.buildCodeBlock() {
		UseImports.addImports(KtorQualifiers.PACKAGE_WEBSOCKET, "webSocket")
		addStatement("this.httpClient.webSocket(")
		indent()
		addStatement("urlString = %S,", parseToFullUrl())
		val hasBearerAuth = functionModels.any { it is BearerAuthModel }
		if (hasBearerAuth) {
			buildBearerAuth()
		}
		unindent()
		buildBlock()
	}
	
	private fun parseToFullUrl(): String {
		val url = functionModels.filterIsInstance<ApiModel>().first().url
		if (url.isWSOrWSS()) return url
		val apiUrl = apiStructure.url.removeSuffix("/")
		return "$apiUrl/${url.removePrefix("/")}"
	}
	
	private fun CodeBlock.Builder.buildBearerAuth() {
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "bearerAuth")
		addStatement("request = {")
		indent()
		addStatement("this@${className.simpleName}.ktorfit.token?.invoke()?.let { bearerAuth(it) }")
		unindent()
		addStatement("}")
	}
	
	private fun CodeBlock.Builder.buildBlock() {
		beginControlFlow(") {")
		val varName = parameterModels.first().varName
		addStatement("with($varName) { handle() }")
		endControlFlow()
	}
}