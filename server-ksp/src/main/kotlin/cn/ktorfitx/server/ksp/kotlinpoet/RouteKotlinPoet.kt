package cn.ktorfitx.server.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.buildFileSpec
import cn.ktorfitx.common.ksp.util.builders.buildFunSpec
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilderLocal
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.constants.PackageNames
import cn.ktorfitx.server.ksp.model.*
import com.squareup.kotlinpoet.*

internal object RouteKotlinPoet {
	
	private var index = 0
	
	fun getFileSpec(
		generatorModel: RouteGeneratorModel,
		functionModels: List<FunctionModel>
	): FileSpec {
		index = 0
		return buildFileSpec(generatorModel.packageName, generatorModel.fileName) {
			fileSpecBuilderLocal.set(this)
			indent("\t")
			addFunction(getFunctionSpec(generatorModel.funName, functionModels))
			fileSpecBuilderLocal.remove()
		}
	}
	
	private fun getFunctionSpec(
		funName: String,
		functionModels: List<FunctionModel>
	): FunSpec {
		return buildFunSpec(funName) {
			receiver(ClassNames.Routing)
			addCode(getCodeBlock(functionModels))
		}
	}
	
	private fun getCodeBlock(functionModels: List<FunctionModel>): CodeBlock {
		return buildCodeBlock {
			functionModels.forEach { functionModel ->
				buildAuthenticationIfNeed(functionModel) { routeModel ->
					when (routeModel) {
						is HttpRequestModel -> {
							fileSpecBuilder.addImport(PackageNames.KTOR_ROUTING, routeModel.method)
							beginControlFlow(
								"""
								${routeModel.method}(
									path = %S
								)
								""".trimIndent(),
								routeModel.path
							)
							val alias = getLetterSequence(index++)
							val memberName = MemberName(functionModel.canonicalName, functionModel.funName, true)
							fileSpecBuilder.addAliasedImport(memberName, alias)
							addStatement("val result = $alias()")
							fileSpecBuilder.addImport(PackageNames.KTOR_RESPONSE, "respond")
							addStatement("call.respond(result)")
							endControlFlow()
						}
						
						is WebSocketModel -> {
							if (routeModel.negotiateExtensions != null) {
								fileSpecBuilder.addImport(PackageNames.KTOR_WEB_SOCKET, "webSocketRaw")
								beginControlFlow(
									"""
									webSocketRaw(
										path = %S,
										protocol = %S,
										negotiateExtensions = %L
									)
									""".trimIndent(),
									routeModel.path,
									routeModel.protocol.ifBlank { null },
									routeModel.negotiateExtensions
								)
							} else {
								fileSpecBuilder.addImport(PackageNames.KTOR_WEB_SOCKET, "webSocket")
								beginControlFlow(
									"""
									webSocket(
										path = %S,
										protocol = %S
									)
									""".trimIndent(),
									routeModel.path,
									routeModel.protocol.ifBlank { null }
								)
							}
							val alias = getLetterSequence(index++)
							val memberName = MemberName(functionModel.canonicalName, functionModel.funName, true)
							fileSpecBuilder.addAliasedImport(memberName, alias)
							addStatement("$alias()")
							endControlFlow()
						}
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.buildAuthenticationIfNeed(
		functionModel: FunctionModel,
		block: (RouteModel) -> Unit
	) {
		if (functionModel.authenticationModel != null) {
			val configurations = functionModel.authenticationModel.configurations
			val strategy = functionModel.authenticationModel.strategy
			fileSpecBuilder.addImport(PackageNames.KTOR_AUTH, "authenticate")
			beginControlFlow(
				"""
				authenticate(
					configurations = ${if (configurations.isEmpty()) "arrayOf(null)," else "arrayOf($configurations.joinToString()}),"}
					strategy = %T
				)
				""".trimIndent(),
				strategy
			)
		}
		block(functionModel.routeModel)
		if (functionModel.authenticationModel != null) {
			endControlFlow()
		}
	}
	
	private fun getLetterSequence(index: Int): String {
		var num = index
		val builder = StringBuilder()
		do {
			builder.append(('a' + (num % 26)))
			num = num / 26 - 1
		} while (num >= 0)
		return builder.reverse().toString()
	}
}