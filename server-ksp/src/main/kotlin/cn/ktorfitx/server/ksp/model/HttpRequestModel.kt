package cn.ktorfitx.server.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.ClassName

internal class HttpRequestModel(
	override val path: String,
	val className: ClassName,
	override val annotation: KSAnnotation
) : RouteModel