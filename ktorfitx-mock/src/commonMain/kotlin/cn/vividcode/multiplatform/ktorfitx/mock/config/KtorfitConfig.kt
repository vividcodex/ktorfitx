package cn.vividcode.multiplatform.ktorfitx.mock.config

import cn.vividcode.multiplatform.ktorfitx.core.config.KtorfitConfig
import cn.vividcode.multiplatform.ktorfitx.mock.MockClient

private val mockClientMap = mutableMapOf<KtorfitConfig, MockClient>()

val KtorfitConfig.mockClient: MockClient
	get() = mockClientMap.getOrPut(this) { MockClient() }

/**
 * MockClient 扩展
 */
fun KtorfitConfig.mockClient(
	builder: MockClientConfig.() -> Unit
) {
	val config = MockClientConfig().apply(builder).build()
	mockClientMap[this] = MockClient(config.log!!, config.json!!)
}