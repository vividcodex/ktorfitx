package cn.ktorfitx.multiplatform.ksp.constants

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName

internal object TypeNames {
	
	val Unit = ClassName("kotlin", "Unit")
	
	val Nothing = ClassName("kotlin", "Nothing")
	
	val Result = ClassName("kotlin", "Result")
	
	val ByteArray = ClassName("kotlin", "ByteArray")
	
	val String = ClassName("kotlin", "String")
	
	val OptIn = ClassName("kotlin", "OptIn")
	
	val ByteReadChannel = ClassName("kotlinx.io", "ByteReadChannel")
	
	val SynchronizedObject = ClassName("io.ktor.utils.io.locks", "SynchronizedObject")
	
	val CancellationException = ClassName("io.ktor.utils.io", "CancellationException")
	
	val AttributeKey = ClassName("io.ktor.util", "AttributeKey")
	
	val InternalAPI = ClassName("io.ktor.utils.io", "InternalAPI")
	
	val Api = ClassName("cn.ktorfitx.multiplatform.annotation", "Api")
	
	val DefaultApiScope = ClassName("cn.ktorfitx.multiplatform.core.scope", "DefaultApiScope")
	
	val ApiScope = ClassName("cn.ktorfitx.multiplatform.annotation", "ApiScope")
	
	val ApiScopes = ClassName("cn.ktorfitx.multiplatform.annotation", "ApiScopes")
	
	val WebSocket = ClassName("cn.ktorfitx.multiplatform.annotation", "WebSocket")
	
	val BearerAuth = ClassName("cn.ktorfitx.multiplatform.annotation", "BearerAuth")
	
	val Headers = ClassName("cn.ktorfitx.multiplatform.annotation", "Headers")
	
	val GET = ClassName("cn.ktorfitx.multiplatform.annotation", "GET")
	
	val POST = ClassName("cn.ktorfitx.multiplatform.annotation", "POST")
	
	val PUT = ClassName("cn.ktorfitx.multiplatform.annotation", "PUT")
	
	val DELETE = ClassName("cn.ktorfitx.multiplatform.annotation", "DELETE")
	
	val HEAD = ClassName("cn.ktorfitx.multiplatform.annotation", "HEAD")
	
	val PATCH = ClassName("cn.ktorfitx.multiplatform.annotation", "PATCH")
	
	val OPTIONS = ClassName("cn.ktorfitx.multiplatform.annotation", "OPTIONS")
	
	val Mock = ClassName("cn.ktorfitx.multiplatform.annotation", "Mock")
	
	val Body = ClassName("cn.ktorfitx.multiplatform.annotation", "Body")
	
	val Header = ClassName("cn.ktorfitx.multiplatform.annotation", "Header")
	
	val Cookie = ClassName("cn.ktorfitx.multiplatform.annotation", "Cookie")
	
	val Attribute = ClassName("cn.ktorfitx.multiplatform.annotation", "Attribute")
	
	val Field = ClassName("cn.ktorfitx.multiplatform.annotation", "Field")
	
	val Query = ClassName("cn.ktorfitx.multiplatform.annotation", "Query")
	
	val Part = ClassName("cn.ktorfitx.multiplatform.annotation", "Part")
	
	val Path = ClassName("cn.ktorfitx.multiplatform.annotation", "Path")
	
	private val DefaultClientWebSocketSession = ClassName("io.ktor.client.plugins.websocket", "DefaultClientWebSocketSession")
	
	val DefaultClientWebSocketSessionLambda = LambdaTypeName.get(
		receiver = DefaultClientWebSocketSession,
		returnType = Unit
	).copy(suspending = true)
	
	val WebSocketSessionHandler = ClassName("cn.ktorfitx.multiplatform.websockets", "WebSocketSessionHandler")
	
	val MockProvider = ClassName("cn.ktorfitx.multiplatform.mock", "MockProvider")
	
	val Ktorfitx = ClassName("cn.ktorfitx.multiplatform.core", "Ktorfitx")
	
	val KtorfitxConfig = ClassName("cn.ktorfitx.multiplatform.core.config", "KtorfitxConfig")
}