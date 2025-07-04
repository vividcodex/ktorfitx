package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.expends.isWSOrWSS
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.ApiModel
import cn.ktorfitx.multiplatform.ksp.model.model.BearerAuthModel
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
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
		fileSpecBuilder.addImport(PackageNames.KTOR_WEBSOCKET, "webSocket")
		addStatement("this.config.httpClient!!.webSocket(")
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
		val apiUrl = apiStructure.url
		if (apiUrl == null) return url
		return "$apiUrl/$url"
	}
	
	private fun CodeBlock.Builder.buildBearerAuth() {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "bearerAuth")
		addStatement("request = {")
		indent()
		addStatement("this@${className.simpleName}.config.token?.invoke()?.let { bearerAuth(it) }")
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