package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Authentication
import cn.ktorfitx.server.annotation.Header
import cn.ktorfitx.server.annotation.POST
import io.ktor.server.routing.*

@Authentication
@POST("header/test1")
fun RoutingContext.headerTest1(
	@Header contentType: String,
	@Header authentication: String?
): String = ""