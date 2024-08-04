package cn.vividcode.multiplatform.ktor.client.api.builder.mock

import cn.vividcode.multiplatform.ktor.client.api.annotation.BuilderDsl

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午9:33
 *
 * 介绍：MocksConfig
 */
@BuilderDsl
sealed interface MocksConfig {
	
	fun <T : Any> api(url: String, block: MockGroupDsl<T>.() -> Unit)
}

internal class MocksConfigImpl : MocksConfig {
	
	val groupMocksMap = mutableMapOf<String, MutableMap<String, MockModel<*>>>()
	
	override fun <T : Any> api(url: String, block: MockGroupDsl<T>.() -> Unit) {
		if (url.isBlank()) {
			error("url 不能为空")
		}
		val mockGroupDsl = MockGroupDslImpl<T>().apply(block)
		if (mockGroupDsl.enabled) {
			val mockGroupMap = groupMocksMap.getOrPut(url) { mutableMapOf() }
			mockGroupMap += mockGroupDsl.mockModels
		}
	}
}