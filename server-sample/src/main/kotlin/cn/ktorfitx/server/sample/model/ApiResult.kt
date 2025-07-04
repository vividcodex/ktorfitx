package cn.ktorfitx.server.sample.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResult<T : Any>(
	val code: Int,
	val msg: String,
	val data: T?
)