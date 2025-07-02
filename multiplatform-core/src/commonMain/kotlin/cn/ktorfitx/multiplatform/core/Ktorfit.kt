package cn.ktorfitx.multiplatform.core

import cn.ktorfitx.multiplatform.core.config.KtorfitConfig
import cn.ktorfitx.multiplatform.core.scope.ApiScope
import cn.ktorfitx.multiplatform.core.scope.DefaultApiScope
import kotlin.jvm.JvmName

class Ktorfit<AS : ApiScope> internal constructor(
	val config: KtorfitConfig
)

/**
 * ktorfit
 */
fun ktorfit(
	config: KtorfitConfig.() -> Unit,
): Ktorfit<DefaultApiScope> = KtorfitConfig()
	.apply(config)
	.build()

/**
 * ktorfit
 */
@JvmName("ktorfitCustom")
fun <AS : ApiScope> ktorfit(
	config: KtorfitConfig.() -> Unit
): Ktorfit<AS> = KtorfitConfig()
	.apply(config)
	.build()