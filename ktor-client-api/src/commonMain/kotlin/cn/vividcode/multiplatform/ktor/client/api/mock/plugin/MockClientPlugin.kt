package cn.vividcode.multiplatform.ktor.client.api.mock.plugin

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/7 上午3:56
 *
 * 介绍：MockClientPlugin
 */
internal sealed interface MockClientPlugin<out TConfig : Any, TPlugin : Any> {
	
	fun install(block: TConfig.() -> Unit): TPlugin
}