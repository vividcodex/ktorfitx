package cn.ktorfitx.server.ksp.model

internal class FunModel(
	val funName: String,
	val canonicalName: String,
	val isExtension: Boolean,
	val group: String?,
	val authenticationModel: AuthenticationModel?,
	val routeModel: RouteModel,
	val varNames: List<String>,
	var principalModels: List<PrincipalModel>
)