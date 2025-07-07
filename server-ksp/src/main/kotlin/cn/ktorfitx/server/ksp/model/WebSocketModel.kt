package cn.ktorfitx.server.ksp.model

internal open class WebSocketModel(
	override val path: String,
	open val protocol: String
) : RouteModel