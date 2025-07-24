package cn.ktorfitx.server.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation

internal class HttpRequestModel(
	override val path: String,
	override val annotation: KSAnnotation,
	val method: String,
	val isCustom: Boolean
) : RouteModel