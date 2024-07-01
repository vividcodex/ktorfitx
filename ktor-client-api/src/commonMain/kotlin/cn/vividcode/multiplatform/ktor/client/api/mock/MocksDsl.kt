package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorBuilderDsl
import kotlin.reflect.KClass

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
interface MocksDsl {
	
	fun <T : Any> group(kClass: KClass<T>, block: MockGroupDsl<T>.() -> Unit)
}

internal class MocksDslImpl : MocksDsl {
	
	val mocksMap = mutableMapOf<KClass<*>, MutableMap<String, MockModel<*>>>()
	
	override fun <T : Any> group(kClass: KClass<T>, block: MockGroupDsl<T>.() -> Unit) {
		val mockGroupDsl = MockGroupDslImpl<T>().apply(block)
		if (mockGroupDsl.enabled) {
			mocksMap[kClass] = mockGroupDsl.mockModels
		}
	}
}

inline fun <reified T : Any> MocksDsl.group(
	noinline block: MockGroupDsl<T>.() -> Unit
) {
	this.group(T::class, block)
}