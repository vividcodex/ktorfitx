package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.ClassName

internal class CookieModel(
	val name: String,
	val varName: String,
	val isNullable: Boolean,
	val encoding: ClassName
)