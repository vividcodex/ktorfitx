package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class RouteGenerator(
	val includeGroups: Array<String> = [],
	val excludeGroups: Array<String> = [],
	val funName: String = "generateRoutes",
)