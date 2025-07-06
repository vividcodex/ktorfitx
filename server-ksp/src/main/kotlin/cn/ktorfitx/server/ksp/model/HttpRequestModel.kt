package cn.ktorfitx.server.ksp.model

internal class HttpRequestModel(
	override val path: String,
	val method: String
) : RouteModel