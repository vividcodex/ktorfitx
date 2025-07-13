package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Body
import cn.ktorfitx.server.annotation.POST
import kotlinx.serialization.Serializable

@POST("body/test1")
fun bodyTest1(
	@Body body: BodyTest1
): BodyTest1 = body

@Serializable
data class BodyTest1(
	val test: String
)