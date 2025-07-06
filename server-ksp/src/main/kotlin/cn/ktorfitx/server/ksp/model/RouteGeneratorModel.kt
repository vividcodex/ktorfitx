package cn.ktorfitx.server.ksp.model

internal class RouteGeneratorModel(
	val includeGroups: Array<String>,
	val excludeGroups: Array<String>,
	val packageName: String,
	val fileName: String,
	val funName: String
)