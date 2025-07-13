package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Authentication
import cn.ktorfitx.server.annotation.WebSocket
import cn.ktorfitx.server.annotation.WebSocketRaw
import io.ktor.server.websocket.*

@WebSocket("/websocket/test1")
fun DefaultWebSocketServerSession.testWebSocket1() {

}

@Authentication
@WebSocket("/websocket/test2")
fun DefaultWebSocketServerSession.testWebSocket2() {

}

object WebSocketTest1 {
	
	@WebSocketRaw("/websocket/test3")
	fun WebSocketServerSession.testWebSocket3() {
	
	}
	
	object WebSocketTest02 {
		
		@Authentication
		@WebSocketRaw("/websocket/test4")
		fun WebSocketServerSession.testWebSocket4() {
		
		}
	}
}