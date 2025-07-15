package cn.ktorfitx.server.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Attribute(
	val name: String = ""
)