package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocket(
	val path: String,
	val protocol: String = ""
)