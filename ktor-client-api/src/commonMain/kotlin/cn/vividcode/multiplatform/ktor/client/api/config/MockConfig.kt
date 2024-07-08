package cn.vividcode.multiplatform.ktor.client.api.config

import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MockModel

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
	val groupMocksMap: MutableMap<String, MutableMap<String, MockModel<*>>> = mutableMapOf(),
) {
	
	internal fun addGroupMocksMap(
		groupMocksMap: MutableMap<String, MutableMap<String, MockModel<*>>>
	) {
		groupMocksMap.forEach { (url, mocksMap) ->
			val map = this.groupMocksMap.getOrPut(url) { mutableMapOf() }
			mocksMap.forEach { (name, mock) ->
				map[name] = mock
			}
		}
	}
}