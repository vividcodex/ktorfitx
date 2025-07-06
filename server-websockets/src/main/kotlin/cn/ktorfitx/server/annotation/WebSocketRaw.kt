package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocketRaw(
	val path: String,
	val protocol: String = "",
	val negotiateExtensions: Boolean = false
)