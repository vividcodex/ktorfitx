package cn.ktorfitx.server.sample.model

import kotlinx.serialization.Serializable

@Serializable
data class Custom(
	val token: String,
)