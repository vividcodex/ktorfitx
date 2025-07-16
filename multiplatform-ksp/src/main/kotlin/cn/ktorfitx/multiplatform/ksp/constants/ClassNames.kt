package cn.ktorfitx.multiplatform.ksp.constants

import com.squareup.kotlinpoet.ClassName

internal object ClassNames {
	
	val Unit = ClassName.bestGuess("kotlin.Unit")
	
	val Nothing = ClassName.bestGuess("kotlin.Nothing")
	
	val Result = ClassName.bestGuess("kotlin.Result")
	
	val ByteArray = ClassName.bestGuess("kotlin.ByteArray")
	
	val ByteReadChannel = ClassName.bestGuess("kotlinx.io.ByteReadChannel")
	
	val SynchronizedObject = ClassName("io.ktor.utils.io.locks", "SynchronizedObject")
	
	val CancellationException = ClassName.bestGuess("io.ktor.utils.io.CancellationException")
	
	val String = ClassName.bestGuess("kotlin.String")
	
	val Api = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Api")
	
	val OptIn = ClassName("kotlin", "OptIn")
	
	val InternalAPI = ClassName("io.ktor.utils.io", "InternalAPI")
	
	val DefaultApiScope = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.scope.DefaultApiScope")
	
	val ApiScope = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.ApiScope")
	
	val ApiScopes = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.ApiScopes")
	
	val WebSocket = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.WebSocket")
	
	val BearerAuth = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.BearerAuth")
	
	val Headers = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Headers")
	
	val GET = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.GET")
	
	val POST = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.POST")
	
	val PUT = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.PUT")
	
	val DELETE = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.DELETE")
	
	val HEAD = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.HEAD")
	
	val PATCH = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.PATCH")
	
	val OPTIONS = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.OPTIONS")
	
	val Mock = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Mock")
	
	val Body = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Body")
	
	val Header = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Header")
	
	val Cookie = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Cookie")
	
	val Field = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Field")
	
	val Query = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Query")
	
	val Part = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Part")
	
	val Path = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Path")
	
	val WebSocketSessionHandler = ClassName.bestGuess("cn.ktorfitx.multiplatform.websockets.WebSocketSessionHandler")
	
	val MockProvider = ClassName.bestGuess("cn.ktorfitx.multiplatform.mock.MockProvider")
	
	val Ktorfitx = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.Ktorfitx")
	
	val KtorfitxConfig = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.config.KtorfitxConfig")
}