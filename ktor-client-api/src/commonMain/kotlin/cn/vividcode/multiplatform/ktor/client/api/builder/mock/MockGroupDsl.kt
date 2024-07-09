package cn.vividcode.multiplatform.ktor.client.api.builder.mock

import kotlin.time.Duration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午1:34
 *
 * 介绍：MockGroupConfig
 */
@MockBuilderDsl
sealed interface MockGroupDsl<T : Any> {
	
	var enabled: Boolean
	
	fun mock(name: String = MOCK_DEFAULT_NAME, block: MockDsl<T>.() -> Unit)
}

internal class MockGroupDslImpl<T : Any> : MockGroupDsl<T> {
	
	val mockModels = mutableMapOf<String, MockModel<*>>()
	
	override var enabled: Boolean = true
	
	override fun mock(name: String, block: MockDsl<T>.() -> Unit) {
		if (name.isBlank()) {
			error("Mock 的名称不能为空")
		}
		val mockDsl = MockDslImpl<T>().apply(block)
		if (mockDsl.enabled) {
			val result = mockDsl.result
			val durationRange = when {
				mockDsl.durationRange == DurationRange.ZERO && mockDsl.duration == Duration.ZERO -> DurationRange.DEFAULT
				mockDsl.durationRange != DurationRange.ZERO -> mockDsl.durationRange
				mockDsl.duration != Duration.ZERO -> mockDsl.duration .. mockDsl.duration
				else -> error("不允许同时使用 duration 和 durationRange 两个参数配置")
			}
			mockModels[name.trim()] = MockModel(durationRange, result)
		}
	}
}

internal const val MOCK_DEFAULT_NAME = "DEFAULT"