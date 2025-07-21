package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.*

@WebSocket("/websocket/test1")
fun testWebSocket1(
	@Query name: String,
	@Query("custom") name2: String
) {

}

@WebSocket("/websocket/{id}")
fun testWebSocket2(
	@Path id: Int
) {

}

@Authentication
@WebSocket("/websocket/test2")
fun testWebSocket2() {

}

object WebSocketTest1 {
	
	@WebSocketRaw("/websocket/test3")
	fun testWebSocket3() {
	
	}
	
	object WebSocketTest02 {
		
		@Authentication
		@WebSocketRaw("/websocket/test4")
		fun testWebSocket4() {
		
		}
	}
}