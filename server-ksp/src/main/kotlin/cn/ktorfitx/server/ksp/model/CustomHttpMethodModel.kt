package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.ClassName

internal class CustomHttpMethodModel(
	val method: String,
	val className: ClassName
)