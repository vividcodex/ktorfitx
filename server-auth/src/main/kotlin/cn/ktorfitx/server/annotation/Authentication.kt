package cn.ktorfitx.server.annotation

import io.ktor.server.auth.*

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Authentication(
	val configurations: Array<String> = [],
	val strategy: AuthenticationStrategy = AuthenticationStrategy.FirstSuccessful,
)