package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class RouteGenerator(
	val groupNames: Array<String> = [],
	val funName: String = "generateRoutes",
)