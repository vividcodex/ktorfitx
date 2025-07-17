package cn.ktorfitx.multiplatform.websockets

import io.ktor.client.plugins.websocket.*

/**
 * WebSocketSessionHandler
 */
typealias WebSocketSessionHandler = suspend DefaultClientWebSocketSession.() -> Unit