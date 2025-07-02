package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OPTIONS(
	val url: String
)