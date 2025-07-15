package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Attribute
import cn.ktorfitx.server.annotation.POST


@POST("attribute/test1")
fun attributeTest1(
	@Attribute value: String,
	@Attribute("custom") value2: Int
): String = ""

@POST("attribute/test2")
fun attributeTest2(
	@Attribute value: Boolean,
	@Attribute("custom") value2: Float
): Boolean = value