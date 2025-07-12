package cn.ktorfitx.server.ksp.model

internal class WebSocketRawModel(
	override val path: String,
	val protocol: String,
	val negotiateExtensions: Boolean
) : RouteModel