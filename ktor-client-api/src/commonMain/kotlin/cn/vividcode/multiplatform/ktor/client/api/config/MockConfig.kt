package cn.vividcode.multiplatform.ktor.client.api.config

import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MockModel

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 17:50
 *
 * 文件介绍：MockConfig
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