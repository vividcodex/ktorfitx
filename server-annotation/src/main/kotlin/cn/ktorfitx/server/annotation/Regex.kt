package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Regex(
	vararg val options: RegexOption
)