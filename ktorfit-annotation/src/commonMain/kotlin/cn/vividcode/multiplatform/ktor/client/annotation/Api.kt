package cn.vividcode.multiplatform.ktorfit.annotation

import cn.vividcode.multiplatform.ktorfit.scope.ApiScope
import cn.vividcode.multiplatform.ktorfit.scope.DefaultApiScope
import kotlin.reflect.KClass

/**
 * 项目名称：vividcode-multiplatform-ktorfit
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
	val apiScope: KClass<out ApiScope> = DefaultApiScope::class
)