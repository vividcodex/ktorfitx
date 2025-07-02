package cn.ktorfitx.multiplatform.sample.http

import kotlinx.serialization.Serializable

@Serializable
data class TestRequest2(
	val param1: String
)