package cn.ktorfitx.server.ksp.constants

import com.squareup.kotlinpoet.ClassName

internal object ClassNames {
	
	val routes by lazy {
		arrayOf(GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, WebSocket, WebSocketRaw)
	}
	
	val partDatas by lazy {
		arrayOf(FormItem, FileItem, BinaryItem, BinaryChannelItem)
	}
	
	val parameterAnnotations by lazy {
		arrayOf(Principal, Body, Field, Path, Query, PartForm, PartFile, PartBinary, PartBinaryChannel, Header)
	}
	
	val String = ClassName.bestGuess("kotlin.String")
	
	val ByteArray = ClassName.bestGuess("kotlin.ByteArray")
	
	val GET = ClassName.bestGuess("cn.ktorfitx.server.annotation.GET")
	
	val POST = ClassName.bestGuess("cn.ktorfitx.server.annotation.POST")
	
	val PUT = ClassName.bestGuess("cn.ktorfitx.server.annotation.PUT")
	
	val DELETE = ClassName.bestGuess("cn.ktorfitx.server.annotation.DELETE")
	
	val PATCH = ClassName.bestGuess("cn.ktorfitx.server.annotation.PATCH")
	
	val HEAD = ClassName.bestGuess("cn.ktorfitx.server.annotation.HEAD")
	
	val OPTIONS = ClassName.bestGuess("cn.ktorfitx.server.annotation.OPTIONS")
	
	val Group = ClassName.bestGuess("cn.ktorfitx.server.annotation.Group")
	
	val WebSocket = ClassName.bestGuess("cn.ktorfitx.server.annotation.WebSocket")
	
	val WebSocketRaw = ClassName.bestGuess("cn.ktorfitx.server.annotation.WebSocketRaw")
	
	val Authentication = ClassName.bestGuess("cn.ktorfitx.server.annotation.Authentication")
	
	val RouteGenerator = ClassName.bestGuess("cn.ktorfitx.server.annotation.RouteGenerator")
	
	val Principal = ClassName.bestGuess("cn.ktorfitx.server.annotation.Principal")
	
	val Body = ClassName.bestGuess("cn.ktorfitx.server.annotation.Body")
	
	val Field = ClassName.bestGuess("cn.ktorfitx.server.annotation.Field")
	
	val PartForm = ClassName.bestGuess("cn.ktorfitx.server.annotation.PartForm")
	
	val PartFile = ClassName.bestGuess("cn.ktorfitx.server.annotation.PartFile")
	
	val PartBinary = ClassName.bestGuess("cn.ktorfitx.server.annotation.PartBinary")
	
	val PartBinaryChannel = ClassName.bestGuess("cn.ktorfitx.server.annotation.PartBinaryChannel")
	
	val FormItem = ClassName.bestGuess("io.ktor.http.content.PartData.FormItem")
	
	val FileItem = ClassName.bestGuess("io.ktor.http.content.PartData.FileItem")
	
	val BinaryItem = ClassName.bestGuess("io.ktor.http.content.PartData.BinaryItem")
	
	val BinaryChannelItem = ClassName.bestGuess("io.ktor.http.content.PartData.BinaryChannelItem")
	
	val Query = ClassName.bestGuess("cn.ktorfitx.server.annotation.Query")
	
	val Path = ClassName.bestGuess("cn.ktorfitx.server.annotation.Path")
	
	val Header = ClassName.bestGuess("cn.ktorfitx.server.annotation.Header")
	
	val RoutingContext = ClassName.bestGuess("io.ktor.server.routing.RoutingContext")
	
	val Routing = ClassName.bestGuess("io.ktor.server.routing.Routing")
	
	val WebSocketServerSession = ClassName.bestGuess("io.ktor.server.websocket.WebSocketServerSession")
	
	val DefaultWebSocketServerSession = ClassName.bestGuess("io.ktor.server.websocket.DefaultWebSocketServerSession")
}