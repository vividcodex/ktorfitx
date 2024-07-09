package cn.vividcode.multiplatform.ktor.client.api.mock.plugin

import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MockModel
import cn.vividcode.multiplatform.ktor.client.api.mock.MockDsl

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/7 上午4:30
 *
 * 介绍：MockCache
 */
@MockDsl
sealed interface MockCache {
	
	val groupMocksMap: Map<String, Map<String, MockModel<*>>>
	
	@MockDsl
	sealed interface Config {
		
		var groupMocksMap: Map<String, Map<String, MockModel<*>>>
	}
	
	private class ConfigImpl : Config {
		
		override var groupMocksMap: Map<String, Map<String, MockModel<*>>> = mapOf()
	}
	
	companion object : MockClientPlugin<Config, MockCache> {
		
		override fun install(block: Config.() -> Unit): MockCache {
			val config = ConfigImpl().apply(block)
			return MockCacheImpl(config.groupMocksMap)
		}
	}
}

@MockDsl
private class MockCacheImpl(
	override val groupMocksMap: Map<String, Map<String, MockModel<*>>>
) : MockCache

@Suppress("UNCHECKED_CAST")
fun <T : Any> MockCache.getMockModel(url: String, name: String): MockModel<T> {
	return groupMocksMap[url]?.get(name) as? MockModel<T>
		?: error("在 $url 中未找到名为 $name 的 Mock")
}