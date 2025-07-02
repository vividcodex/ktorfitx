package cn.ktorfitx.server.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class OPTIONS(
	val path: String
)