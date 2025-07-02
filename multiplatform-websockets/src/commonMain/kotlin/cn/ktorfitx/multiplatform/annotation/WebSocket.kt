package cn.ktorfitx.multiplatform.annotation

/**
 * WebSocket
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocket(
	val url: String
)