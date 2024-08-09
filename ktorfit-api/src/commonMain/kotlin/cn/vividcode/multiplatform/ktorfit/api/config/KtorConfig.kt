package cn.vividcode.multiplatform.ktorfit.api.config

import io.ktor.client.plugins.logging.*

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/14 21:07
 *
 * 文件介绍：HttpConfig
 */
internal class KtorConfig internal constructor(
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