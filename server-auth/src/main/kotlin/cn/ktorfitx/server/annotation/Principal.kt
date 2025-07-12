package cn.ktorfitx.server.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Principal(
	val provider: String = ""
)