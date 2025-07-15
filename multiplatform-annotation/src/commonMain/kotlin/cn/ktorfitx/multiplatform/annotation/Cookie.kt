package cn.ktorfitx.multiplatform.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Cookie(
	val name: String = "",
	val maxAge: Int = 0,
	val expires: Long = -1L,
	val domain: String = "",
	val path: String = "",
	val secure: Boolean = false,
	val httpOnly: Boolean = false,
	val extensions: Array<String> = [],
)