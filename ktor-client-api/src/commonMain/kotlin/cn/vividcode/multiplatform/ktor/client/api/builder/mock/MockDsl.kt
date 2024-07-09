package cn.vividcode.multiplatform.ktor.client.api.builder.mock

import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import kotlin.time.Duration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午5:28
 *
 * 介绍：MockDsl
 */
@MockBuilderDsl
sealed interface MockDsl<T : Any> {
	
	/**
	 * 关闭延迟
	 */
	val closing: DurationRange
	
	/**
	 * 无限延迟
	 */
	val infinite: DurationRange
	
	/**
	 * enabled
	 */
	var enabled: Boolean
	
	/**
	 * duration
	 */
	var duration: Duration
	
	/**
	 * durationRange
	 */
	var durationRange: DurationRange
	
	/**
	 * rangeTo
	 */
	operator fun Duration.rangeTo(other: Duration): DurationRange
	
	/**
	 * result
	 */
	var result: T
	
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
	
	override val closing: DurationRange by lazy { DurationRange.ZERO }
	
	override val infinite: DurationRange by lazy { DurationRange.INFINITE }
	
	override var enabled: Boolean = true
	
	override var duration: Duration = Duration.ZERO
	
	override var durationRange: DurationRange = DurationRange.ZERO
	
	override fun Duration.rangeTo(other: Duration): DurationRange = DurationRange(this, other)
	
	override lateinit var result: T
	
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