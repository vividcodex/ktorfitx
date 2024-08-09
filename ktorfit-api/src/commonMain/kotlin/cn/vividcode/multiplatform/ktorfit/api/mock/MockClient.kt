package cn.vividcode.multiplatform.ktorfit.api.mock

import cn.vividcode.multiplatform.ktorfit.api.annotation.BuilderDsl
import cn.vividcode.multiplatform.ktorfit.api.mock.plugin.MockClientPlugin

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 17:43
 *
 * 文件介绍：MockClient
 */
@BuilderDsl
class MockClient internal constructor(
	private val pluginMap: Map<MockClientPlugin<*, *>, Any>
) {
	
	@Suppress("UNCHECKED_CAST")
	internal fun <TConfig : Any, TPlugin : Any> getPlugin(
		plugin: MockClientPlugin<TConfig, TPlugin>
	): TPlugin = this.pluginMap[plugin] as? TPlugin ?: error("未找到 MockClient 插件")
}

@BuilderDsl
internal fun MockClient(
	block: MockClientConfig.() -> Unit
): MockClient {
	val config = MockClientConfigImpl().apply(block)
	return MockClient(config.pluginMap)
}

@BuilderDsl
internal sealed interface MockClientConfig {
	
	fun <TConfig : Any, TPlugin : Any> install(
		plugin: MockClientPlugin<TConfig, TPlugin>,
		block: (TConfig.() -> Unit)? = null
	)
}

private class MockClientConfigImpl : MockClientConfig {
	
	val pluginMap = mutableMapOf<MockClientPlugin<*, *>, Any>()
	
	override fun <TConfig : Any, TPlugin : Any> install(
		plugin: MockClientPlugin<TConfig, TPlugin>,
		block: (TConfig.() -> Unit)?
	) {
		this.pluginMap[plugin] = plugin.install { block?.invoke(this) }
	}
}