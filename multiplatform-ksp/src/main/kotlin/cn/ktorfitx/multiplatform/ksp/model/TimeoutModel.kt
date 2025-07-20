package cn.ktorfitx.multiplatform.ksp.model

internal class TimeoutModel(
	val requestTimeoutMillis: Long?,
	val connectTimeoutMillis: Long?,
	val socketTimeoutMillis: Long?,
)