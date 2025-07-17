package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.WebSocket
import cn.ktorfitx.multiplatform.websockets.WebSocketSessionHandler
import io.ktor.client.plugins.websocket.*

@Api
interface TestWebSocket2Api {
	
	@WebSocket("test")
	suspend fun test(handler: suspend DefaultClientWebSocketSession.() -> Unit)
	
	@WebSocket("/test2")
	suspend fun test2(handler: WebSocketSessionHandler)
}