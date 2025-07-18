package cn.ktorfitx.multiplatform.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Timeout(
	val requestTimeoutMillis: Long = -1L,
	val connectTimeoutMillis: Long = -1L,
	val socketTimeoutMillis: Long = -1L,
)