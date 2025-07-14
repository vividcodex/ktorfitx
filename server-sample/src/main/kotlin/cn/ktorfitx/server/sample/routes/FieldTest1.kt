package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Field
import cn.ktorfitx.server.annotation.GET
import cn.ktorfitx.server.annotation.POST

@GET("field/test1")
fun fieldTest1(
	@Field test1: String,
	@Field("custom") test2: Int
): String = ""

@POST("field/test2")
fun fieldTest2(
	@Field test1: String,
	@Field test2: String?
): String = ""

@POST("field/test3")
fun fieldTest3(
	@Field parameters: String
): String = ""