package cn.vividcode.multiplatform.ktor.client.api.builder.mock

import kotlin.jvm.JvmInline

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/28 下午2:55
 *
 * 介绍：MockDelay
 */
@JvmInline
value class MockDelay internal constructor(
	val range: LongRange
)