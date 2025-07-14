package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.ClassName

internal class HttpRequestModel(
	override val path: String,
	val className: ClassName
) : RouteModel