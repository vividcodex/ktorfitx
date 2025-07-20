package cn.ktorfitx.multiplatform.ksp.model

import com.squareup.kotlinpoet.ClassName

internal class HttpRequestModel(
	override val url: String,
	val className: ClassName,
) : RouteModel