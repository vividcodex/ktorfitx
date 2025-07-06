package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.TypeName

internal class FunctionModel(
	val funName: String,
	val canonicalName: String,
	val group: String?,
	val returnType: TypeName,
	val authenticationModel: AuthenticationModel?,
	val routeModel: RouteModel
)