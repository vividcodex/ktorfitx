package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorBuilderDsl

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午5:28
 *
 * 介绍：MockDsl
 */
@KtorBuilderDsl
sealed interface MockDsl<T : Any> {
	
	/**
	 * enabled
	 */
	var enabled: Boolean
	
	/**
	 * name
	 */
	var name: String
	
	/**
	 * delay
	 */
	var delay: MockDelay
	
	/**
	 * mock
	 */
	var mock: T?
	
	/**
	 * LongRange.delay
	 */
	val LongRange.delay: MockDelay
	
	/**
	 * Long.delay
	 */
	val Long.delay: MockDelay
}

internal class MockDslImpl<T : Any> : MockDsl<T> {
	
	override var enabled: Boolean = true
	
	override var name: String = ""
	
	override var delay: MockDelay = MockDelay.Default
	
	override var mock: T? = null
	
	override val LongRange.delay: MockDelay
		get() = MockDelay.range(this)
	
	override val Long.delay: MockDelay
		get() = MockDelay.fixed(this)
}