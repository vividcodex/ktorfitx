package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class HttpMethod(
	val method: String = ""
)