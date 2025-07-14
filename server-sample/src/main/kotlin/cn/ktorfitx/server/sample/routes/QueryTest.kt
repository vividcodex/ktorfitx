package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.GET
import cn.ktorfitx.server.annotation.POST
import cn.ktorfitx.server.annotation.Query

@GET("query/test1")
fun queryTest1(
	@Query test1: String,
	@Query("custom") test2: Int
): String = ""

@POST("query/test2")
fun queryTest2(
	@Query test1: String,
	@Query test2: String?
): String = ""

@POST("query/test3")
fun queryTest3(
	@Query queryParameters: String
): String = ""