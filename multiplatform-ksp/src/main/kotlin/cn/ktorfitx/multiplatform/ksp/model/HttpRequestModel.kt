package cn.ktorfitx.multiplatform.ksp.model

internal class HttpRequestModel(
	override val url: String,
	val method: String,
	val isCustom: Boolean
) : RouteModel