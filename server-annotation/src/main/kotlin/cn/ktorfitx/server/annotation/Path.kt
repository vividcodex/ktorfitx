package cn.ktorfitx.server.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(
	val name: String = "",
	val regex: String = ""
)