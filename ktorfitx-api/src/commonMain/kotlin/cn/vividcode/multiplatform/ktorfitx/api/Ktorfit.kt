package cn.vividcode.multiplatform.ktorfitx.api

import cn.vividcode.multiplatform.ktorfitx.api.config.KtorfitConfig
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import cn.vividcode.multiplatform.ktorfitx.api.scope.DefaultApiScope

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/9 23:52
 *
 * 文件介绍：Ktorfit
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Ktorfit<AS : ApiScope> internal constructor(
	val config: KtorfitConfig,
	private val apiScope: AS,
)

/**
 * ktorfit
 */
fun ktorfit(
	config: KtorfitConfig.() -> Unit,
): Ktorfit<DefaultApiScope> = KtorfitConfig()
	.apply(config)
	.build(DefaultApiScope)

/**
 * ktorfit
 */
fun <AS : ApiScope> ktorfit(
	apiScope: AS,
	config: KtorfitConfig.() -> Unit,
): Ktorfit<AS> = KtorfitConfig()
	.apply(config)
	.build(apiScope)