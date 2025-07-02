package cn.vividcode.multiplatform.ktorfitx.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DELETE(
	val url: String
)