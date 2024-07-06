package cn.vividcode.multiplatform.ktor.client.api.config

import cn.vividcode.multiplatform.ktor.client.api.mock.MockModel
import kotlin.reflect.KFunction

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/6 下午5:50
 *
 * 介绍：MockConfig
 */
internal class MockConfig(
	private val groupMocksMap: MutableMap<KFunction<*>, MutableMap<String, MockModel<*>>> = mutableMapOf(),
) {
	
	internal fun addGroupMocksMap(
		groupMocksMap: MutableMap<KFunction<*>, MutableMap<String, MockModel<*>>>
	) {
		groupMocksMap.forEach { (kFunction, mocksMap) ->
			this.groupMocksMap.getOrPut(kFunction) { mutableMapOf() }.let {
				mocksMap.forEach { (name, mock) ->
					it[name] = mock
				}
			}
		}
	}
	
	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> getMockModel(
		kFunction1: KFunction<T>,
		kFunction2: KFunction<T>,
		mockName: String
	): MockModel<T>? {
		return (groupMocksMap[kFunction1]?.get(mockName)
			?: groupMocksMap[kFunction2]?.get(mockName)) as? MockModel<T>
	}
}