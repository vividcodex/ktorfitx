@file:RouteGenerator

package cn.ktorfitx.server.sample.plugins

import cn.ktorfitx.server.annotation.RouteGenerator
import cn.ktorfitx.server.sample.plugins.generators.generateRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Application.configureRoutes() {
	install(WebSockets)
	routing {
		generateRoutes()
	}
	
}