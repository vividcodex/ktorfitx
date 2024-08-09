package cn.vividcode.multiplatform.ktorfit.api.mock.plugin

import cn.vividcode.multiplatform.ktorfit.api.annotation.BuilderDsl
import cn.vividcode.multiplatform.ktorfit.api.builder.mock.MockModel
import kotlin.reflect.KClass

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/7 4:30
 *
 * 文件介绍：MockCache
 */
@BuilderDsl
sealed interface MockCache {
	
	val groupMocksMap: Map<String, Map<String, MockModel<*>>>
	
	@BuilderDsl
	sealed interface Config {
		
		var groupMocksMap: Map<String, Map<String, MockModel<*>>>
	}
	
	private data class ConfigImpl(
		override var groupMocksMap: Map<String, Map<String, MockModel<*>>> = emptyMap()
	) : Config

	companion object : MockClientPlugin<Config, MockCache> {
		
		override fun install(block: Config.() -> Unit): MockCache {
			val (groupMocksMap) = ConfigImpl().apply(block)
			return MockCacheImpl(groupMocksMap)
		}
	}
}

@BuilderDsl
private class MockCacheImpl(
	override val groupMocksMap: Map<String, Map<String, MockModel<*>>>
) : MockCache

@Suppress("UNCHECKED_CAST")
fun <T : Any> MockCache.getMockModel(url: String, name: String, kClass: KClass<T>): MockModel<T> {
	val mockModel = groupMocksMap[url]?.get(name)
	check(mockModel != null) { "在 $url 中未找到名为 $name 的 Mock" }
	return mockModel as? MockModel<T> ?: error("在 $url 中找到名为 $name 的 Mock, 但是类型不匹配，需要的类型是: ${kClass.simpleName}, 实际的是 ${mockModel::class.simpleName}")
	return groupMocksMap[url]?.get(name) as? MockModel<T>
		?: error("在 $url 中未找到名为 $name 的 Mock")
}