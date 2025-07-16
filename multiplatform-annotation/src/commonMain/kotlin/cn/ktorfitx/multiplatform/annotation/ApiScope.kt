package cn.ktorfitx.multiplatform.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ApiScope(
	val apiScope: KClass<*>
)