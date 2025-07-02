package cn.ktorfitx.multiplatform.websockets

import io.ktor.client.plugins.websocket.*

/**
 * WebSocketSessionHandler
 */
fun interface WebSocketSessionHandler {
	
	suspend fun DefaultClientWebSocketSession.handle()
}