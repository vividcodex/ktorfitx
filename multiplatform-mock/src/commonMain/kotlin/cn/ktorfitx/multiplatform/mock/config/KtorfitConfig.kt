package cn.ktorfitx.multiplatform.mock.config

import cn.ktorfitx.multiplatform.core.config.KtorfitxConfig
import cn.ktorfitx.multiplatform.mock.MockClient

private val mockClientMap = mutableMapOf<KtorfitxConfig, MockClient>()

val KtorfitxConfig.mockClient: MockClient
	get() = mockClientMap.getOrPut(this) { MockClient() }

/**
 * MockClient 扩展
 */
fun KtorfitxConfig.mockClient(
	builder: MockClientConfig.() -> Unit
) {
	val config = MockClientConfig().apply(builder).build()
	mockClientMap[this] = MockClient(config.log!!, config.format)
}