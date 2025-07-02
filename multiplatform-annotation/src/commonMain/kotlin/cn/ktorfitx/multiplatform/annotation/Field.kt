package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Field(
	val name: String = ""
)