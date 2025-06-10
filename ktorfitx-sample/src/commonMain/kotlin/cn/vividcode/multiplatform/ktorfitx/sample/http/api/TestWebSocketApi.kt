package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.Api
import io.ktor.client.plugins.websocket.*

@Api(url = "test4")
interface TestWebSocketApi {

//	@BearerAuth
//	@WebSocket("test1")
//	suspend fun test1(block: suspend DefaultClientWebSocketSession.() -> Unit)
//
//	@WebSocket("ws://localhost:8080/test4/test2")
//	suspend fun test2(block: WebSocketSessionBlock) {
//		test2 {
//
//		}
//	}
}

typealias WebSocketSessionBlock = suspend DefaultClientWebSocketSession.() -> Unit