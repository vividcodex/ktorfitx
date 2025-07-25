package cn.ktorfitx.server.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.server.ksp.constants.PackageNames
import cn.ktorfitx.server.ksp.constants.TypeNames
import cn.ktorfitx.server.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock

internal class RouteCodeBlock(
	private val funModel: FunModel
) {
	
	private val varNames = funModel.varNames.toMutableSet()
	private var partVarName: String? = null
	private var beforePartDispose = true
	
	fun CodeBlock.Builder.addCodeBlock(funName: String) {
		addPrincipalsCodeBlock()
		addQueriesCodeBlock()
		addPathsCodeBlock()
		addHeadersCodeBlock()
		addCookiesCodeBlock()
		addAttributesCodeBlock()
		addRequestBodyCodeBlock()
		addFunCodeBlock(funName)
	}
	
	private fun CodeBlock.Builder.addPrincipalsCodeBlock() {
		val principalModels = funModel.principalModels
		if (principalModels.isEmpty()) return
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_AUTH, "principal")
		principalModels.forEach {
			val nullSafety = if (it.isNullable) "" else "!!"
			if (it.provider != null) {
				addStatement("val %N = this.call.principal<%T>(%S)%L", it.varName, it.typeName, it.provider, nullSafety)
			} else {
				addStatement("val %N = this.call.principal<%T>()%L", it.varName, it.typeName, nullSafety)
			}
		}
	}
	
	private fun CodeBlock.Builder.addQueriesCodeBlock() {
		val queryModels = funModel.queryModels
		if (queryModels.isEmpty()) return
		val varName = getVarName("queryParameters")
		addStatement("val %N = this.call.request.queryParameters", varName)
		queryModels.forEach {
			when {
				it.isNullable -> addStatement("val %N = %L[%S]", it.varName, varName, it.name)
				else -> {
					fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_UTIL, "getOrFail")
					if (it.typeName == TypeNames.String) {
						addStatement("val %N = %N.getOrFail(%S)", it.varName, varName, it.name)
					} else {
						addStatement("val %N = %N.getOrFail<%T>(%S)", it.varName, varName, it.typeName, it.name)
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.addPathsCodeBlock() {
		val pathModels = funModel.pathModels
		if (pathModels.isEmpty()) return
		val isWebSocket = funModel.routeModel !is HttpRequestModel
		val varName = getVarName(if (isWebSocket) "parameters" else "pathParameters")
		addStatement("val %N = this.call.%N", varName, if (isWebSocket) "parameters" else "pathParameters")
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_UTIL, "getOrFail")
		pathModels.forEach {
			if (it.typeName == TypeNames.String) {
				addStatement("val %N = %N.getOrFail(%S)", it.varName, varName, it.name)
			} else {
				addStatement("val %N = %N.getOrFail<%T>(%S)", it.varName, varName, it.typeName, it.name)
			}
		}
	}
	
	private fun CodeBlock.Builder.addHeadersCodeBlock() {
		val headerModels = funModel.headerModels
		if (headerModels.isEmpty()) return
		val varName = getVarName("headers")
		addStatement("val %N = this.call.request.headers", varName)
		headerModels.forEach {
			addStatement("val %N = %N[%S]%L", it.varName, varName, it.name, if (it.isNullable) "" else "!!")
		}
	}
	
	private fun CodeBlock.Builder.addCookiesCodeBlock() {
		val cookieModels = funModel.cookieModels
		if (cookieModels.isEmpty()) return
		val varName = getVarName("cookies")
		addStatement("val %N = this.call.request.cookies", varName)
		cookieModels.forEach {
			if (it.encoding == TypeNames.CookieEncodingURIEncoding) {
				addStatement("val %N = %N[%S]%L", it.varName, varName, it.name, if (it.isNullable) "" else "!!")
			} else {
				addStatement("val %N = %N[%S, %T]%L", it.varName, varName, it.name, it.encoding, if (it.isNullable) "" else "!!")
			}
		}
	}
	
	private fun CodeBlock.Builder.addAttributesCodeBlock() {
		val attributeModels = funModel.attributeModels
		if (attributeModels.isEmpty()) return
		val varName = getVarName("attributes")
		addStatement("val %N = this.call.attributes", varName)
		fileSpecBuilder.addImport(PackageNames.KTOR_UTIL, "AttributeKey")
		attributeModels.forEach {
			if (it.isNullable) {
				addStatement("val %N = %L.getOrNull(AttributeKey<%T>(%S))", it.varName, varName, it.typeName, it.name)
			} else {
				addStatement("val %N = %L[AttributeKey<%T>(%S)]", it.varName, varName, it.typeName, it.name)
			}
		}
	}
	
	private fun CodeBlock.Builder.addRequestBodyCodeBlock() {
		if (funModel.requestBodyModel == null) return
		when (funModel.requestBodyModel) {
			is BodyModel -> addBodyCodeBlock(funModel.requestBodyModel)
			is FieldModels -> addFieldsCodeBlock(funModel.requestBodyModel.fieldModels)
			is PartModels -> addPartsCodeBlock(funModel.requestBodyModel.partModels)
		}
	}
	
	private fun CodeBlock.Builder.addBodyCodeBlock(
		bodyModel: BodyModel
	) {
		if (bodyModel.isNullable) {
			fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_REQUEST, "receiveNullable")
			addStatement("val %N = this.call.receiveNullable<%T>()", bodyModel.varName, bodyModel.typeName)
		} else {
			fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_REQUEST, "receive")
			addStatement("val %N = this.call.receive<%T>()", bodyModel.varName, bodyModel.typeName)
		}
	}
	
	private fun CodeBlock.Builder.addFieldsCodeBlock(
		fieldModels: List<FieldModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_REQUEST, "receiveParameters")
		val varName = getVarName("parameters")
		addStatement("val %N = this.call.receiveParameters()", varName)
		fieldModels.forEach {
			when {
				it.isNullable -> addStatement("val %N = %N[%S]", it.varName, varName, it.name)
				else -> {
					fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_UTIL, "getOrFail")
					if (it.typeName == TypeNames.String) {
						addStatement("val %N = %N.getOrFail(%S)", it.varName, varName, it.name)
					} else {
						addStatement("val %N = %N.getOrFail<%T>(%S)", it.varName, varName, it.typeName, it.name)
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.addPartsCodeBlock(
		partModels: List<PartModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTORFITX_SERVER_CORE, "resolve")
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_REQUEST, "receiveMultipart")
		partVarName = getVarName("multipartParameters")
		addStatement("val %N = this.call.receiveMultipart().resolve()", partVarName)
		partModels.forEach {
			if (beforePartDispose) {
				beforePartDispose = !it.isPartData
			}
			val orNull = if (it.isNullable) "OrNull" else ""
			val funName = when (it.annotation) {
				TypeNames.PartForm -> "getForm${if (it.isPartData) "" else "Value"}$orNull"
				TypeNames.PartFile -> "getFile${if (it.isPartData) "" else "ByteArray"}$orNull"
				TypeNames.PartBinary -> "getBinary${if (it.isPartData) "" else "ByteArray"}$orNull"
				TypeNames.PartBinaryChannel -> "getBinaryChannel$orNull"
				else -> error("不支持的类型 ${it.annotation}")
			}
			addStatement("val %N = %N.%N(%S)", it.varName, partVarName, funName, it.name)
		}
	}
	
	private fun CodeBlock.Builder.addFunCodeBlock(
		funName: String
	) {
		val parameters = funModel.varNames.joinToString()
		if (funModel.routeModel is HttpRequestModel) {
			if (partVarName != null && beforePartDispose) {
				addStatement("%N.disposeAll()", partVarName)
			}
			
			fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_RESPONSE, "respond")
			val varName = getVarName("result")
			addStatement("val %N = %N(%L)", varName, funName, parameters)
			if (partVarName != null && !beforePartDispose) {
				addStatement("%N.disposeAll()", partVarName)
			}
			fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "HttpStatusCode")
			addStatement("this.call.respond(HttpStatusCode.OK, %N)", varName)
		} else {
			addStatement("%N(%L)", funName, parameters)
		}
	}
	
	private fun getVarName(varName: String): String {
		var i = 0
		var name = varName
		while (name in varNames) {
			name = varName + i++
		}
		varNames += name
		return name
	}
}