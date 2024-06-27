package cn.vividcode.multiplatform.ktor.client.api.config

import io.ktor.client.plugins.logging.*
import kotlin.reflect.KClass

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午9:07
 *
 * 介绍：HttpConfig
 */
internal data class HttpConfig internal constructor(
	var connectTimeout: Long = 5000L,
	var socketTimeout: Long = Long.MAX_VALUE,
	var keepAliveTime: Long = 5000L,
	var logLevel: LogLevel = LogLevel.HEADERS,
	var handleLog: (message: String) -> Unit = {},
	var mockMap: Map<KClass<*>, Any> = emptyMap()
)