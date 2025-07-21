package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.GET
import cn.ktorfitx.server.annotation.POST
import cn.ktorfitx.server.annotation.Path

@POST("path/test1/{parent}/{custom}")
fun pathTest1(
	@Path parent: String,
	@Path("custom") child: Int
): String = ""

@GET("path/test2/{id}")
fun pathTest2(
	@Path id: Int
): String = ""

@GET("path/test2/{id}")
fun pathTest3(
	@Path(regex = "[0-9]+") id: Int
): String = ""