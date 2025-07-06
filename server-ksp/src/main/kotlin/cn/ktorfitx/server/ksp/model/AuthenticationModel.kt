package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.ClassName

internal class AuthenticationModel(
	val configurations: Array<String>,
	val strategy: ClassName
)