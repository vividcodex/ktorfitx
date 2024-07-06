package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.mock.MockModel
import cn.vividcode.multiplatform.ktor.client.api.mock.MocksDsl
import cn.vividcode.multiplatform.ktor.client.api.mock.MocksDslImpl
import kotlin.reflect.KFunction

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午3:55
 *
 * 介绍：MockBuilderDsl
 */
internal class MockBuilderDelegate {
	
	fun mocks(
		allGroupMocksMap: MutableMap<KFunction<*>, MutableMap<String, MockModel<*>>>,
		block: MocksDsl.() -> Unit
	) {
		val groupMockMap = MocksDslImpl().apply(block).groupMocksMap
		groupMockMap.forEach { (function, mocksMap) ->
			val mocksGroup = allGroupMocksMap.getOrPut(function) { mutableMapOf() }
			mocksMap.forEach { (name, mock) ->
				mocksGroup[name] = mock
			}
		}
	}
}