@file:RouteGenerator(
	includeGroups = ["group2"],
	funName = "generateRoutes2",
)

package cn.ktorfitx.server.sample.plugins

import cn.ktorfitx.server.annotation.RouteGenerator
import cn.ktorfitx.server.sample.plugins.generators.generateRoutes2
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes2() {
	routing {
		generateRoutes2()
	}
}