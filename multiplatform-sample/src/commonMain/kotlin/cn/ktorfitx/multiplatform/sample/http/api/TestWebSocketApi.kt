package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.BearerAuth
import cn.ktorfitx.multiplatform.annotation.GET
import cn.ktorfitx.multiplatform.annotation.WebSocket
import cn.ktorfitx.multiplatform.websockets.WebSocketSessionHandler

@Api(url = "test4")
interface TestWebSocketApi {
	
	@BearerAuth
	@WebSocket("test1")
	suspend fun test1(block: WebSocketSessionHandler)
	
	@WebSocket("ws://localhost:8080/test4/test2")
	suspend fun test2(handler: WebSocketSessionHandler)
	
	@GET("test3")
	suspend fun test3(): Result<String>
}