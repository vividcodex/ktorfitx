package cn.vividcode.multiplatform.ktor.client.api.builder.mock

import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/9 21:45
 *
 * 文件介绍：DurationRange
 */
data class DurationRange internal constructor(
	val first: Duration,
	val last: Duration
) {
	
	internal companion object {
		
		internal val INFINITE by lazy { Duration.INFINITE .. Duration.INFINITE }
		
		internal val ZERO by lazy { Duration.ZERO .. Duration.ZERO }
		
		internal val DEFAULT by lazy { 100.milliseconds .. 300.milliseconds }
	}
	
	/**
	 * random
	 */
	fun random(): Duration {
		try {
			val random = Random.nextLong(first.inWholeMilliseconds .. last.inWholeMilliseconds)
			return random.milliseconds
		} catch (e: IllegalArgumentException) {
			throw NoSuchElementException(e.message)
		}
	}
}

/**
 * rangeTo
 */
internal operator fun Duration.rangeTo(other: Duration): DurationRange {
	return DurationRange(this, other)
}