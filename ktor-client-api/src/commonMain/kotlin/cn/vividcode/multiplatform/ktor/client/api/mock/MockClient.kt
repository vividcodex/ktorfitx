package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.config.MockConfig
import kotlinx.coroutines.delay
import kotlin.reflect.KFunction

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/6 下午5:43
 *
 * 介绍：MockClient
 */
@Suppress("unused")
class MockClient internal constructor(
	private val mockConfig: MockConfig,
	private val handleLog: (message: String) -> Unit
) {
	
	/**
	 * 获取 Mock
	 */
	suspend fun <T : Any> getMock(
		kFunction1: KFunction<T>,
		kFunction2: KFunction<T>,
		mockName: String
	): T {
		val mockModel = this.mockConfig.getMockModel(kFunction1, kFunction2, mockName) ?: let {
			if (mockName.isEmpty()) {
				error("${kFunction1.name} 没有默认的 Mock")
			} else {
				error("${kFunction1.name} 没有名为 $mockName")
			}
		}
		delay(mockModel.delayRange.random())
		handleLog()
		return mockModel.mock as? T ?: error("返回类型和 Mock 数据类型不匹配")
	}
	
	private fun handleLog() {
	
	}
}