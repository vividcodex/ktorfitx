package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.Api
import cn.vividcode.multiplatform.ktorfitx.websockets.WebSocket
import cn.vividcode.multiplatform.ktorfitx.websockets.WebSocketSessionHandler

@Api
interface TestWebSocket2Api {
	
	@WebSocket("test")
	suspend fun test(handler: WebSocketSessionHandler)
	
	@WebSocket("/test2")
	suspend fun test2(handler: WebSocketSessionHandler)
}