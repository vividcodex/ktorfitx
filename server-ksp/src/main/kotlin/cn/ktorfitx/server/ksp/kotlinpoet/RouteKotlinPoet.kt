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
	
	private val funNames = mutableListOf<String>()
	
	fun getFileSpec(
		generatorModel: RouteGeneratorModel,
		funModels: List<FunModel>
	): FileSpec = buildFileSpec(generatorModel.packageName, generatorModel.fileName) {
		fileSpecBuilderLocal.set(this)
		indent("\t")
		val funSpec = getFunctionSpec(generatorModel.funName, funModels)
		addFunction(funSpec)
		fileSpecBuilderLocal.remove()
	}
	
	private fun getFunctionSpec(
		funName: String,
		funModels: List<FunModel>
	): FunSpec = buildFunSpec(funName) {
		receiver(ClassNames.Routing)
		val codeBlock = getCodeBlock(funModels)
		addCode(codeBlock)
	}
	
	private fun getCodeBlock(
		funModels: List<FunModel>
	): CodeBlock = buildCodeBlock {
		funModels.forEach { funModel ->
			buildAuthenticationIfNeed(funModel) { routeModel ->
				when (routeModel) {
					is HttpRequestModel -> buildHttpRequest(funModel, routeModel)
					is WebSocketRawModel -> buildWebRawSocket(funModel, routeModel)
					is WebSocketModel -> buildWebSocket(funModel, routeModel)
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.buildAuthenticationIfNeed(
		funModel: FunModel,
		block: (RouteModel) -> Unit
	) {
		if (funModel.authenticationModel != null) {
			val configurations = funModel.authenticationModel.configurations
			val strategy = funModel.authenticationModel.strategy
			fileSpecBuilder.addImport(PackageNames.KTOR_AUTH, "authenticate")
			beginControlFlow(
				"""
				authenticate(
					configurations = %L,
					strategy = %T
				)
				""".trimIndent(),
				if (configurations.isEmpty()) "arrayOf(null)" else "arrayOf(${configurations.joinToString()})",
				strategy
			)
		}
		block(funModel.routeModel)
		if (funModel.authenticationModel != null) {
			endControlFlow()
		}
	}
	
	private fun CodeBlock.Builder.buildHttpRequest(
		funModel: FunModel,
		httpRequestModel: HttpRequestModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_ROUTING, httpRequestModel.method)
		beginControlFlow(
			"""
			%N(
				path = %S
			)
			""".trimIndent(),
			httpRequestModel.method,
			httpRequestModel.path
		)
		buildCodeBlock(funModel)
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildWebRawSocket(
		funModel: FunModel,
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
		buildCodeBlock(funModel)
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildWebSocket(
		funModel: FunModel,
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
		buildCodeBlock(funModel)
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildCodeBlock(
		funModel: FunModel
	) {
		val principalModel = funModel.principalModel
		if (principalModel != null) {
			fileSpecBuilder.addImport(PackageNames.KTOR_AUTH, "principal")
			val notNullStr = if (principalModel.isNullable) "" else "!!"
			if (principalModel.provider != null) {
				addStatement("val %N = this.call.principal<%T>(%S)$notNullStr", principalModel.varName, principalModel.typeName, principalModel.provider)
			} else {
				addStatement("val %N = this.call.principal<%T>()$notNullStr", principalModel.varName, principalModel.typeName)
			}
		}
		
		val funName = getFunNameAndImport(funModel)
		if (funModel.routeModel is HttpRequestModel) {
			val varNames = funModel.varNames.joinToString()
			beginControlFlow("%N($varNames).let", funName)
			fileSpecBuilder.addImport(PackageNames.KTOR_RESPONSE, "respond")
			addStatement("this.call.respond(it)")
			endControlFlow()
		} else {
			addStatement("%N()", funName)
		}
	}
	
	private fun getFunNameAndImport(funModel: FunModel): String {
		var i = 0
		var funName = funModel.funName
		while (funName in funNames) {
			funName = funModel.funName + i++
		}
		funNames += funName
		if (i == 0) {
			fileSpecBuilder.addImport(funModel.canonicalName, funModel.funName)
		} else {
			val memberName = MemberName(funModel.canonicalName, funModel.funName, true)
			fileSpecBuilder.addAliasedImport(memberName, funName)
		}
		return funName
	}
}