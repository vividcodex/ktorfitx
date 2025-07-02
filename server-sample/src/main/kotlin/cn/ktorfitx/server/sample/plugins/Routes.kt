package cn.ktorfitx.server.sample.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*

//// 自动生成
//fun Routing.generateRoutes() {
//	authenticate(
//		configurations = arrayOf(null),
//		strategy = AuthenticationStrategy.FirstSuccessful
//	) {
//		post(
//			path = "/auth/verifyToken"
//		) {
//			val principal = call.principal<NoPrincipal>()!!
//			val result = verifyToken(principal)
//			call.respond(result)
//		}
//	}
//}
fun Application.routes() {
	routing {
	
	}
}