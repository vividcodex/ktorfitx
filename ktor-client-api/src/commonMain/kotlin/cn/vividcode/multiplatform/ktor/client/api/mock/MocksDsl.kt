package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorBuilderDsl
import kotlin.reflect.KSuspendFunction1

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午9:33
 *
 * 介绍：MocksDsl
 */
@KtorBuilderDsl
sealed interface MocksDsl {
	
	fun <T : Any> group(function: KSuspendFunction1<*, T>, block: MockGroupDsl<T>.() -> Unit)
}

internal class MocksDslImpl : MocksDsl {
	
	val groupMocksMap = mutableMapOf<KSuspendFunction1<*, *>, MutableMap<String, MockModel<*>>>()
	
	override fun <T : Any> group(function: KSuspendFunction1<*, T>, block: MockGroupDsl<T>.() -> Unit) {
		val mockGroupDsl = MockGroupDslImpl<T>().apply(block)
		if (mockGroupDsl.enabled) {
			val mockGroupMap = groupMocksMap.getOrPut(function) { mutableMapOf() }
			mockGroupMap += mockGroupDsl.mockModels
		}
	}
}