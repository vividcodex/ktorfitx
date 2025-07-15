package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Cookie
import cn.ktorfitx.server.annotation.POST
import io.ktor.server.routing.*

@POST("/cookie/test1")
fun RoutingContext.cookieTest1(
	@Cookie cookie: String,
	@Cookie("custom") cookie2: String?
): String = ""