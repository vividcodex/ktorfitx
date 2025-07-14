package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.POST
import cn.ktorfitx.server.annotation.Path

@POST("path/test1/{parent}/{custom}")
fun pathTest1(
	@Path parent: String,
	@Path("custom") child: Int
) {

}