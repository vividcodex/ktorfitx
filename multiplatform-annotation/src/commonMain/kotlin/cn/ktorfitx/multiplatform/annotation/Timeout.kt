package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Timeout(
	val requestTimeoutMillis: Long = -1L,
	val connectTimeoutMillis: Long = -1L,
	val socketTimeoutMillis: Long = -1L,
)