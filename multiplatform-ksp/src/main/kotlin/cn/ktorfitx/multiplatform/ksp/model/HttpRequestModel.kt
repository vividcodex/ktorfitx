package cn.ktorfitx.multiplatform.ksp.model

internal class HttpRequestModel(
	override val url: Url,
	val method: String,
	val isCustom: Boolean
) : RouteModel