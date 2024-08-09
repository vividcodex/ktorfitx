package cn.vividcode.multiplatform.ktor.client.api.mock.plugin

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/7 3:56
 *
 * 文件介绍：MockClientPlugin
 */
internal sealed interface MockClientPlugin<out TConfig : Any, TPlugin : Any> {
	
	fun install(block: TConfig.() -> Unit): TPlugin
}