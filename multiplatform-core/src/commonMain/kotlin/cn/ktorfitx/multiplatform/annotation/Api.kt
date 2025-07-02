package cn.ktorfitx.multiplatform.annotation

import cn.ktorfitx.multiplatform.core.scope.ApiScope
import cn.ktorfitx.multiplatform.core.scope.DefaultApiScope
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Api(
	val url: String = "",
	val apiScope: KClass<out ApiScope> = DefaultApiScope::class,
	val apiScopes: Array<KClass<out ApiScope>> = [],
)