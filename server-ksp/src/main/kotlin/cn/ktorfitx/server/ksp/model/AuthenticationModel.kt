package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

internal class AuthenticationModel(
	override val functionClassName: ClassName,
	override val requestMethod: String,
	override val path: String,
	override val returnTypeName: TypeName,
	val configurations: Array<String>,
	val strategy: ClassName
) : RouteModel(functionClassName, requestMethod, path, returnTypeName)