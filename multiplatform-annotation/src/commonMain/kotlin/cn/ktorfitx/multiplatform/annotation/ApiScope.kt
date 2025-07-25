package cn.ktorfitx.multiplatform.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ApiScope(
	val apiScope: KClass<*>
)