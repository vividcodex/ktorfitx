package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.expends.isWSOrWSS
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.ApiModel
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock

internal class WebSocketCodeBuilder(
	classStructure: ClassStructure,
	funStructure: FunStructure
) {
	
	private val apiStructure = classStructure.apiStructure
	private val funModels = funStructure.funModels
	private val parameterModels = funStructure.parameterModels
	
	fun CodeBlock.Builder.buildCodeBlock(
		tokenVarName: String?
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_WEBSOCKET, "webSocket")
		addStatement("this.config.httpClient!!.webSocket(")
		indent()
		addStatement("urlString = %S,", parseToFullUrl())
		if (tokenVarName != null) {
			buildBearerAuth(tokenVarName)
		}
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
	
	private fun CodeBlock.Builder.buildBearerAuth(
		tokenVarName: String
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "bearerAuth")
		addStatement("request = {")
		indent()
		addStatement("%N?.let { bearerAuth(it) }", tokenVarName)
		unindent()
		addStatement("},")
	}
	
	private fun CodeBlock.Builder.buildBlock() {
		val varName = parameterModels.first().varName
		addStatement("block = %N", varName)
	}
}