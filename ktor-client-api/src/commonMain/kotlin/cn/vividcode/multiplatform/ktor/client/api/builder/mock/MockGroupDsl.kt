package cn.vividcode.multiplatform.ktor.client.api.builder.mock

import cn.vividcode.multiplatform.ktor.client.api.annotation.BuilderDsl
import kotlin.time.Duration

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 13:34
 *
 * 文件介绍：MockGroupConfig
 */
@BuilderDsl
sealed interface MockGroupDsl<T : Any> {
	
	var enabled: Boolean
	
	fun mock(name: String = "DEFAULT", block: MockDsl<T>.() -> T)
}

internal class MockGroupDslImpl<T : Any> : MockGroupDsl<T> {
	
	val mockModels = mutableMapOf<String, MockModel<*>>()
	
	override var enabled: Boolean = true
	
	override fun mock(name: String, block: MockDsl<T>.() -> T) {
		if (name.isBlank()) {
			error("Mock 的名称不能为空")
		}
		val mockDsl = MockDslImpl<T>()
		val result = mockDsl.let(block)
		if (mockDsl.enabled) {
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