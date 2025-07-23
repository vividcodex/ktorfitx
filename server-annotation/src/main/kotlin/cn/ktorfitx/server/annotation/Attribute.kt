package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Attribute(
	val name: String = ""
)