package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Authentication
import cn.ktorfitx.server.annotation.GET
import cn.ktorfitx.server.annotation.Principal
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Authentication
@GET(path = "principal/test01")
fun RoutingContext.testPrincipal01(
	@Principal principal: UserIdPrincipal
): String = ""

@Authentication
@GET(path = "principal/test02")
fun RoutingContext.testPrincipal02(
	@Principal principal: UserIdPrincipal?
): String = ""

@Authentication
@GET(path = "principal/test03")
fun RoutingContext.testPrincipal03(
	@Principal("custom") principal: UserIdPrincipal
): String = ""

@Authentication
@GET(path = "principal/test03")
fun RoutingContext.testPrincipal04(
	@Principal("custom") principal: UserIdPrincipal?
): String = ""