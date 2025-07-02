package cn.vividcode.multiplatform.ktorfitx.annotation

import cn.vividcode.multiplatform.ktorfitx.core.scope.ApiScope
import cn.vividcode.multiplatform.ktorfitx.core.scope.DefaultApiScope
import kotlin.reflect.KClass

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 21:06
 *
 * 文件介绍：Api
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Api(
	val url: String = "",
	val apiScope: KClass<out ApiScope> = DefaultApiScope::class,
	val apiScopes: Array<KClass<out ApiScope>> = [],
)