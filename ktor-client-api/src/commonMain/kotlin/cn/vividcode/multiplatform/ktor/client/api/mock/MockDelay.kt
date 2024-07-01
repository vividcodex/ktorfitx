package cn.vividcode.multiplatform.ktor.client.api.mock

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/28 下午2:55
 *
 * 介绍：MockDelay
 */
class MockDelay private constructor(
	val range: LongRange
) {
	
	companion object {
		
		/**
		 * 关闭延迟
		 */
		val Closing by lazy { range(0L .. 0L) }
		
		/**
		 * 无限延迟
		 */
		val Infinite by lazy { range(Long.MAX_VALUE .. Long.MAX_VALUE) }
		
		/**
		 * 默认延迟
		 */
		val Default by lazy { range(100L .. 300L) }
		
		/**
		 * 固定延迟
		 */
		fun fixed(delay: Long): MockDelay = MockDelay(delay .. delay)
		
		/**
		 * 范围延迟
		 */
		fun range(range: LongRange): MockDelay = MockDelay(range)
	}
}