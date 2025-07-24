package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(
	val name: String = ""
)