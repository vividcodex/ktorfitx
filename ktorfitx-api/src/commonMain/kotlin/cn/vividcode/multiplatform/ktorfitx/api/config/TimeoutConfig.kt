package cn.vividcode.multiplatform.ktorfitx.api.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/10 19:10
 *
 * 文件介绍：TimeoutConfig
 */
@KtorfitDsl
class TimeoutConfig internal constructor() {
	
	var connectTimeoutMillis = TimeoutDefaults.CONNECT_TIMEOUT_MILLIS
	
	var requestTimeoutMillis = TimeoutDefaults.REQUEST_TIMEOUT_MILLIS
	
	var socketTimeoutMillis = TimeoutDefaults.SOCKET_TIMEOUT_MILLIS
}

private object TimeoutDefaults {
	
	const val CONNECT_TIMEOUT_MILLIS = 10_000L
	
	const val REQUEST_TIMEOUT_MILLIS = 10_000L
	
	const val SOCKET_TIMEOUT_MILLIS = Long.MAX_VALUE
}