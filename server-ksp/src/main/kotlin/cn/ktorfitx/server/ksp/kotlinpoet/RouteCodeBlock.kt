package cn.ktorfitx.server.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.constants.PackageNames
import cn.ktorfitx.server.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock

internal class RouteCodeBlock(
	private val funModel: FunModel
) {
	
	private val varNames = funModel.varNames.toMutableSet()
	
	fun CodeBlock.Builder.addCodeBlock(funName: String) {
		addPrincipalsCodeBlock()
		addQueriesCodeBlock()
		addRequestBodyCodeBlock()
		addFunCodeBlock(funName)
	}
	
	private fun CodeBlock.Builder.addPrincipalsCodeBlock() {
		val principalModels = funModel.principalModels
		if (principalModels.isNotEmpty()) {
			fileSpecBuilder.addImport(PackageNames.KTOR_AUTH, "principal")
			principalModels.forEach {
				val nullSafety = if (it.isNullable) "" else "!!"
				if (it.provider != null) {
					addStatement("val %N = this.call.principal<%T>(%S)%L", it.varName, it.typeName, it.provider, nullSafety)
				} else {
					addStatement("val %N = this.call.principal<%T>()%L", it.varName, it.typeName, nullSafety)
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.addQueriesCodeBlock() {
		val queryModels = funModel.queryModels
		if (queryModels.isNotEmpty()) {
			val varName = getVarName("queryParameters")
			addStatement("val %N = this.call.queryParameters", varName)
			queryModels.forEach {
				when {
					it.isNullable -> addStatement("val %N = %L[%S]", it.varName, varName, it.name)
					else -> {
						fileSpecBuilder.addImport(PackageNames.KTOR_UTIL, "getOrFail")
						if (it.typeName == ClassNames.String) {
							addStatement("val %N = %N.getOrFail(%S)", it.varName, varName, it.name)
						} else {
							addStatement("val %N = %N.getOrFail<%T>(%S)", it.varName, varName, it.typeName, it.name)
						}
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.addRequestBodyCodeBlock() {
		when (funModel.requestBody) {
			is BodyModel -> addBodyCodeBlock(funModel.requestBody)
			is FieldModels -> addFieldsCodeBlock(funModel.requestBody.fieldModels)
		}
	}
	
	private fun CodeBlock.Builder.addBodyCodeBlock(
		bodyModel: BodyModel
	) {
		if (bodyModel.isNullable) {
			fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "receiveNullable")
			addStatement("val %N = this.call.receiveNullable<%T>()", bodyModel.varName, bodyModel.typeName)
		} else {
			fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "receive")
			addStatement("val %N = this.call.receive<%T>()", bodyModel.varName, bodyModel.typeName)
		}
	}
	
	private fun CodeBlock.Builder.addFieldsCodeBlock(
		fieldModels: List<FieldModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "receiveParameters")
		val varName = getVarName("parameters")
		addStatement("val %N = this.call.receiveParameters()", varName)
		fieldModels.forEach {
			when {
				it.isNullable -> {
					addStatement("val %N = %N[%S]", it.varName, varName, it.name)
				}
				else -> {
					fileSpecBuilder.addImport(PackageNames.KTOR_UTIL, "getOrFail")
					if (it.typeName == ClassNames.String) {
						addStatement("val %N = %N.getOrFail(%S)", it.varName, varName, it.name)
					} else {
						addStatement("val %N = %N.getOrFail<%T>(%S)", it.varName, varName, it.typeName, it.name)
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.addFunCodeBlock(funName: String) {
		val parameters = funModel.varNames.joinToString()
		if (funModel.routeModel is HttpRequestModel) {
			beginControlFlow("%N(%L).also", funName, parameters)
			fileSpecBuilder.addImport(PackageNames.KTOR_RESPONSE, "respond")
			addStatement("this.call.respond(it)")
			endControlFlow()
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