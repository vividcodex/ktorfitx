package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class HttpMethod(
	val method: String = ""
)