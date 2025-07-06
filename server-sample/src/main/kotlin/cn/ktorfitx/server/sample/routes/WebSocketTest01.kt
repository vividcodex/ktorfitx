package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Authentication
import cn.ktorfitx.server.annotation.WebSocket
import cn.ktorfitx.server.annotation.WebSocketRaw
import io.ktor.server.websocket.*

@WebSocket("/websocket/test01")
fun DefaultWebSocketServerSession.testWebSocket01() {

}

@Authentication
@WebSocket("/websocket/test02")
fun DefaultWebSocketServerSession.testWebSocket02() {

}

object WebSocketTest01 {
	
	@WebSocketRaw("/websocket/test03")
	fun WebSocketServerSession.testWebSocket03() {
	
	}
	
	object WebSocketTest02 {
		
		@Authentication
		@WebSocketRaw("/websocket/test04")
		fun WebSocketServerSession.testWebSocket04() {
		
		}
	}
}