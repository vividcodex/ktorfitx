package cn.vividcode.multiplatform.ktorfitx.websockets

/**
 * WebSocket
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocket(
	val url: String
)