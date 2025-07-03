package cn.ktorfitx.server.sample.plugins

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