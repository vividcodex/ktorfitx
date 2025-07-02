package cn.ktorfitx.server.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class PUT(
	val path: String
)