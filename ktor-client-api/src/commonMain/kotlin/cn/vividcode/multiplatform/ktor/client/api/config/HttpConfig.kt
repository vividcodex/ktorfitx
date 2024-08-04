package cn.vividcode.multiplatform.ktor.client.api.config

import io.ktor.client.plugins.logging.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午9:07
 *
 * 介绍：HttpConfig
 */
internal class HttpConfig internal constructor(
	var connectTimeout: Long = 5000L,
	var socketTimeout: Long = Long.MAX_VALUE,
	var keepAliveTime: Long = 5000L,
	var logLevel: LogLevel = LogLevel.HEADERS,
	var handleLog: ((message: String) -> Unit)? = null,
	var jsonConfig: JsonConfig = JsonConfig(),
	var showApiScope: Boolean = false
) {
	
	internal fun check() {
		check(connectTimeout >= 0L) {
			"connectTimeout 必须大于等于 0"
		}
		check(socketTimeout >= 0L) {
			"socketTimeout 必须大于等于 0"
		}
		check(keepAliveTime >= 0L) {
			"keepAliveTime 必须大于等于 0"
		}
	}
}

internal class JsonConfig(
	val prettyPrint: Boolean = false,
	val prettyPrintIndent: String = "    "
)