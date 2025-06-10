package cn.vividcode.multiplatform.ktorfitx.websockets

import io.ktor.client.plugins.websocket.*

fun interface WebSocketSessionHandler {
	
	suspend fun handle(block: suspend DefaultClientWebSocketSession.() -> Unit)
}