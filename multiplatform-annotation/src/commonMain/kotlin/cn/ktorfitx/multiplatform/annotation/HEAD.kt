package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class HEAD(
	val url: String = ""
)