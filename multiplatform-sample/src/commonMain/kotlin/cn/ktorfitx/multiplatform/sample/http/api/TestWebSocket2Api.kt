package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.WebSocket
import cn.ktorfitx.multiplatform.websockets.WebSocketSessionHandler

@Api
interface TestWebSocket2Api {
	
	@WebSocket("test")
	suspend fun test(handler: WebSocketSessionHandler)
	
	@WebSocket("/test2")
	suspend fun test2(handler: WebSocketSessionHandler)
}