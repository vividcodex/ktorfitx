package cn.ktorfitx.server.ksp.model

internal class WebSocketRawModel(
	override val path: String,
	override val protocol: String,
	val negotiateExtensions: Boolean
) : WebSocketModel(path, protocol)