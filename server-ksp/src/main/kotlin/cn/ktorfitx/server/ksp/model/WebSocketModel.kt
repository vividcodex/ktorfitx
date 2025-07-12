package cn.ktorfitx.server.ksp.model

internal class WebSocketModel(
	override val path: String,
	val protocol: String
) : RouteModel