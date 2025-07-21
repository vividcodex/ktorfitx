package cn.ktorfitx.server.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation

internal class WebSocketRawModel(
	override val path: String,
	val protocol: String?,
	val negotiateExtensions: Boolean,
	override val annotation: KSAnnotation
) : RouteModel