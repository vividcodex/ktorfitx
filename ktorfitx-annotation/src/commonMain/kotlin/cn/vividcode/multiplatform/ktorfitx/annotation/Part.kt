package cn.vividcode.multiplatform.ktorfitx.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Part(
	val name: String = ""
)