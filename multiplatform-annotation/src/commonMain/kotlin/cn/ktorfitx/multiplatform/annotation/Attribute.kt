package cn.ktorfitx.multiplatform.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Attribute(
	val name: String = ""
)