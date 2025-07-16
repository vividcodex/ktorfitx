package cn.ktorfitx.multiplatform.sample.http

import kotlinx.serialization.Serializable

@Serializable
data class TestResponse(
	val param1: String,
)