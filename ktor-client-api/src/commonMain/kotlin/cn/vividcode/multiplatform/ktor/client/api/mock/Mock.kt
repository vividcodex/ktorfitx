package cn.vividcode.multiplatform.ktor.client.api.mock

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午5:28
 *
 * 介绍：Mock
 */
interface Mock<T : Any> {
	
	var delayRange: IntRange
	
	var failurePercent: Int
	
	fun setSuccessMock(msg: String, data: T? = null)
	
	fun setFailureMock(code: Int, msg: String)
}

internal class MockImpl<T : Any> : Mock<T> {
	
	override var delayRange: IntRange = DelayRange.DefaultDelay
		set(value) {
			check(value.first >= DelayRange.DELAY_MIN && value.first <= value.last) {
				"The delayRange does not meet the requirements."
			}
			field = value
		}
	
	override var failurePercent: Int = FailurePercent.PERCENT_MIN
		set(value) {
			check(value in FailurePercent.PERCENT_MIN .. FailurePercent.PERCENT_MAX) {
				"The value must be between 0 and 100."
			}
			field = value
		}
	
	override fun setFailureMock(code: Int, msg: String) {
		TODO()
	}
	
	override fun setSuccessMock(msg: String, data: T?) {
		TODO()
	}
}

data class SuccessMock<T : Any> internal constructor(
	val data: T?,
	val msg: String,
)

data class FailureMock internal constructor(
	val code: Int,
	val msg: String
)

object DelayRange {
	
	internal const val DELAY_MIN = 0
	
	val InfiniteDelay = Int.MAX_VALUE .. Int.MAX_VALUE
	
	val NoDelay = 0 .. 0
	
	val DefaultDelay = 200 .. 500
}

object FailurePercent {
	
	internal const val PERCENT_MIN = 0
	
	internal const val PERCENT_MAX = 100
}