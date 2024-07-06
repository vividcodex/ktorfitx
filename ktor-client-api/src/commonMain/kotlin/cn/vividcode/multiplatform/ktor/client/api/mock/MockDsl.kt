package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorBuilderDsl
import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody

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
	 * 关闭延迟
	 */
	val closing: MockDelay
	
	/**
	 * 无限延迟
	 */
	val infinite: MockDelay
	
	/**
	 * 默认延迟
	 */
	val default: MockDelay
	
	/**
	 * enabled
	 */
	var enabled: Boolean
	
	/**
	 * delay
	 */
	var delay: MockDelay
	
	/**
	 * mock
	 */
	var mock: T
	
	/**
	 * IntRange.delay
	 */
	val IntRange.mockDelay: MockDelay
	
	/**
	 * LongRange.delay
	 */
	val LongRange.mockDelay: MockDelay
	
	/**
	 * Int.delay
	 */
	val Int.mockDelay: MockDelay
	
	/**
	 * Long.delay
	 */
	val Long.mockDelay: MockDelay
	
	/**
	 * 成功
	 */
	fun success(data: T? = null, msg: String = "操作成功"): ResultBody<T>
	
	/**
	 * 失败
	 */
	fun failure(code: Int, msg: String = "操作失败"): ResultBody<T>
	
	/**
	 * 异常
	 */
	fun <E : Exception> exception(e: E): ResultBody<T>
}

internal class MockDslImpl<T : Any> : MockDsl<T> {
	
	override val closing: MockDelay by lazy { 0L.mockDelay }
	
	override val infinite: MockDelay by lazy { Long.MAX_VALUE.mockDelay }
	
	override val default: MockDelay = (100L .. 300L).mockDelay
	
	override var enabled: Boolean = true
	
	override var delay: MockDelay = this.default
	
	override lateinit var mock: T
	
	override val IntRange.mockDelay: MockDelay
		get() = MockDelay(first.toLong() .. last.toLong())
	
	override val LongRange.mockDelay: MockDelay
		get() = MockDelay(this)
	
	override val Int.mockDelay: MockDelay
		get() = MockDelay(this.toLong() .. this.toLong())
	
	override val Long.mockDelay: MockDelay
		get() = MockDelay(this .. this)
	
	override fun failure(code: Int, msg: String): ResultBody<T> {
		return ResultBody.failure(code, msg)
	}
	
	override fun success(data: T?, msg: String): ResultBody<T> {
		return ResultBody.success(data, msg)
	}
	
	override fun <E : Exception> exception(e: E): ResultBody<T> {
		return ResultBody.exception(e)
	}
}