package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Api(
	val url: String = ""
)