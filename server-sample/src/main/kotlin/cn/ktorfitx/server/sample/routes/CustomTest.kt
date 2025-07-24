package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.HttpMethod

@HttpMethod
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class CUSTOM(
	val path: String
)

@CUSTOM("custom")
fun custom(): String = ""