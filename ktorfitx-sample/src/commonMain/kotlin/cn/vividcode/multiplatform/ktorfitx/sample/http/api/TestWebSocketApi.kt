package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.Api
import cn.vividcode.multiplatform.ktorfitx.annotation.BearerAuth
import cn.vividcode.multiplatform.ktorfitx.annotation.GET
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfitx.websockets.WebSocket
import cn.vividcode.multiplatform.ktorfitx.websockets.WebSocketSessionHandler

@Api(url = "test4")
interface TestWebSocketApi {
	
	@BearerAuth
	@WebSocket("test1")
	suspend fun test1(block: WebSocketSessionHandler)
	
	@WebSocket("ws://localhost:8080/test4/test2")
	suspend fun test2(handler: WebSocketSessionHandler)
	
	@GET("test3")
	suspend fun test3(): ResultBody<String>
}