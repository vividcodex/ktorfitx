package cn.ktorfitx.server.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation

internal class WebSocketModel(
	override val path: String,
	val protocol: String?,
	override val annotation: KSAnnotation
) : RouteModel