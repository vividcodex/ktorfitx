package cn.ktorfitx.server.ksp.model

internal class FunctionModel(
	val funName: String,
	val canonicalName: String,
	val group: String?,
	val authenticationModel: AuthenticationModel?,
	val routeModel: RouteModel
)