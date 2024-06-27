package cn.vividcode.multiplatform.ktor.client.api.mock

import kotlin.reflect.KClass

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午9:33
 *
 * 介绍：MockScope
 */
interface MockScope {
	
	fun <T : Any> addMock(kClass: KClass<T>, block: Mock<T>.() -> Unit)
}

inline fun <reified T : Any> MockScope.addMock(noinline block: Mock<T>.() -> Unit) {
	this.addMock(T::class, block)
}

internal class MockScopeImpl : MockScope {
	
	internal val mockMap = mutableMapOf<KClass<*>, Mock<*>>()
	
	override fun <T : Any> addMock(kClass: KClass<T>, block: Mock<T>.() -> Unit) {
		val mock = MockImpl<T>().apply(block)
		this.mockMap[kClass] = mock
	}
}