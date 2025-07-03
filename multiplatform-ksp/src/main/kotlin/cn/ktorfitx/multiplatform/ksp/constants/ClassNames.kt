package cn.ktorfitx.multiplatform.ksp.constants

import com.squareup.kotlinpoet.ClassName

internal object ClassNames {
	
	val Unit = ClassName.bestGuess("kotlin.Unit")
	
	val ByteArray = ClassName.bestGuess("kotlin.ByteArray")
	
	val String = ClassName.bestGuess("kotlin.String")
	
	val KotlinException = ClassName.bestGuess("kotlin.Exception")
	
	val JavaException = ClassName.bestGuess("java.lang.Exception")
	
	val Api = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Api")
	
	val ApiScope = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.scope.ApiScope")
	
	val DefaultApiScope = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.scope.DefaultApiScope")
	
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
	
	val Field = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Field")
	
	val Query = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Query")
	
	val Part = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Part")
	
	val Path = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.Path")
	
	val ApiResult = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.model.ApiResult")
	
	val WebSocketSessionHandler = ClassName.bestGuess("cn.ktorfitx.multiplatform.websockets.WebSocketSessionHandler")
	
	val MockProvider = ClassName.bestGuess("cn.ktorfitx.multiplatform.mock.MockProvider")
	
	val Ktorfit = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.Ktorfit")
	
	val KtorfitConfig = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.config.KtorfitConfig")
	
	val ExceptionListener = ClassName.bestGuess("cn.ktorfitx.multiplatform.core.exception.ExceptionListener")
	
	val ExceptionListeners = ClassName.bestGuess("cn.ktorfitx.multiplatform.annotation.ExceptionListeners")
}