package cn.ktorfitx.server.sample

import cn.ktorfitx.server.sample.plugins.configureRoutes
import cn.ktorfitx.server.sample.plugins.configureSecurity
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
	EngineMain.main(args)
}

fun Application.module() {
	configureSecurity()
	configureRoutes()
}