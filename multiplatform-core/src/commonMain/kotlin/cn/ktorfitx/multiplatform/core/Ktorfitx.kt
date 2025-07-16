package cn.ktorfitx.multiplatform.core

import cn.ktorfitx.multiplatform.core.config.KtorfitxConfig
import cn.ktorfitx.multiplatform.core.scope.DefaultApiScope
import kotlin.jvm.JvmName

class Ktorfitx<AS : Any> internal constructor(
	val config: KtorfitxConfig
)

/**
 * ktorfitx
 */
fun ktorfitx(
	config: KtorfitxConfig.() -> Unit,
): Ktorfitx<DefaultApiScope> = KtorfitxConfig()
	.apply(config)
	.build()

/**
 * ktorfitx
 */
@JvmName("ktorfitxWithApiScope")
fun <AS : Any> ktorfitx(
	config: KtorfitxConfig.() -> Unit
): Ktorfitx<AS> = KtorfitxConfig()
	.apply(config)
	.build()