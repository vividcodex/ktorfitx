package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Authentication(
	val configurations: Array<String> = [],
	val strategy: AuthenticationStrategy = AuthenticationStrategy.FirstSuccessful,
)