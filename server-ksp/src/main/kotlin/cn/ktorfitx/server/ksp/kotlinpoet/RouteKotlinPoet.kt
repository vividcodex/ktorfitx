package cn.ktorfitx.server.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.buildFileSpec
import cn.ktorfitx.common.ksp.util.builders.buildFunSpec
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilderLocal
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.constants.PackageNames
import cn.ktorfitx.server.ksp.model.*
import com.squareup.kotlinpoet.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class RouteKotlinPoet {
	
	private val fileComment = """
        该文件是由 cn.ktorfitx:server-ksp 在编译期间根据注解生成的代码，
        所有手动修改将会在下次构建时被覆盖，
        若需修改行为，请修改对应的注解或源代码定义，而不是此文件本身。
        
        生成时间：%L
        """.trimIndent()
	
	private val funNames = mutableListOf<String>()
	
	fun getFileSpec(
		generatorModel: RouteGeneratorModel,
		funModels: List<FunModel>
	): FileSpec = buildFileSpec(generatorModel.packageName, generatorModel.fileName) {
		fileSpecBuilderLocal.set(this)
		addFileComment(fileComment, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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
			fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_AUTH, "authenticate")
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
		val method = httpRequestModel.className.simpleName.lowercase()
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_ROUTING, method)
		beginControlFlow(
			"""
			%N(
				path = %S
			)
			""".trimIndent(),
			method,
			httpRequestModel.path
		)
		buildCodeBlock(funModel)
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildWebRawSocket(
		funModel: FunModel,
		webSocketRawModel: WebSocketRawModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_WEBSOCKET, "webSocketRaw")
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
		fileSpecBuilder.addImport(PackageNames.KTOR_SERVER_WEBSOCKET, "webSocket")
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
		RouteCodeBlock(funModel).apply {
			val funName = getFunNameAndImport(funModel)
			addCodeBlock(funName)
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
			val memberName = MemberName(funModel.canonicalName, funModel.funName, funModel.isExtension)
			fileSpecBuilder.addAliasedImport(memberName, funName)
		}
		return funName
	}
}