package cn.ktorfitx.server.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.buildFileSpec
import cn.ktorfitx.common.ksp.util.builders.buildFunSpec
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilderLocal
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.constants.PackageNames
import cn.ktorfitx.server.ksp.model.*
import com.squareup.kotlinpoet.*

internal class RouteKotlinPoet {
	
	private var index = 0
	
	fun getFileSpec(
		generatorModel: RouteGeneratorModel,
		functionModels: List<FunctionModel>
	): FileSpec {
		return buildFileSpec(generatorModel.packageName, generatorModel.fileName) {
			fileSpecBuilderLocal.set(this)
			indent("\t")
			val funSpec = getFunctionSpec(generatorModel.funName, functionModels)
			addFunction(funSpec)
			fileSpecBuilderLocal.remove()
		}
	}
	
	private fun getFunctionSpec(
		funName: String,
		functionModels: List<FunctionModel>
	): FunSpec {
		return buildFunSpec(funName) {
			receiver(ClassNames.Routing)
			val codeBlock = getCodeBlock(functionModels)
			addCode(codeBlock)
		}
	}
	
	private fun getCodeBlock(functionModels: List<FunctionModel>): CodeBlock {
		return buildCodeBlock {
			functionModels.forEach { functionModel ->
				buildAuthenticationIfNeed(functionModel) { routeModel ->
					when (routeModel) {
						is HttpRequestModel -> buildHttpRequest(functionModel, routeModel)
						is WebSocketRawModel -> buildWebRawSocket(functionModel, routeModel)
						is WebSocketModel -> buildWebSocket(functionModel, routeModel)
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
	
	private fun CodeBlock.Builder.buildHttpRequest(
		functionModel: FunctionModel,
		httpRequestModel: HttpRequestModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_ROUTING, httpRequestModel.method)
		beginControlFlow(
			"""
			${httpRequestModel.method}(
				path = %S
			)
			""".trimIndent(),
			httpRequestModel.path
		)
		val alias = getLetterSequence(index++)
		val memberName = MemberName(functionModel.canonicalName, functionModel.funName, true)
		fileSpecBuilder.addAliasedImport(memberName, alias)
		addStatement("val result = $alias()")
		fileSpecBuilder.addImport(PackageNames.KTOR_RESPONSE, "respond")
		addStatement("call.respond(result)")
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildWebRawSocket(
		functionModel: FunctionModel,
		webSocketRawModel: WebSocketRawModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_WEB_SOCKET, "webSocketRaw")
		beginControlFlow(
			"""
			webSocketRaw(
				path = %S,
				protocol = %S,
				negotiateExtensions = %L
			)
			""".trimIndent(),
			webSocketRawModel.path,
			webSocketRawModel.protocol.ifBlank { null },
			webSocketRawModel.negotiateExtensions
		)
		val alias = getLetterSequence(index++)
		val memberName = MemberName(functionModel.canonicalName, functionModel.funName, true)
		fileSpecBuilder.addAliasedImport(memberName, alias)
		addStatement("$alias()")
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildWebSocket(
		functionModel: FunctionModel,
		webSocketModel: WebSocketModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_WEB_SOCKET, "webSocket")
		beginControlFlow(
			"""
			webSocket(
				path = %S,
				protocol = %S
			)
			""".trimIndent(),
			webSocketModel.path,
			webSocketModel.protocol.ifBlank { null }
		)
		val alias = getLetterSequence(index++)
		val memberName = MemberName(functionModel.canonicalName, functionModel.funName, true)
		fileSpecBuilder.addAliasedImport(memberName, alias)
		addStatement("$alias()")
		endControlFlow()
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