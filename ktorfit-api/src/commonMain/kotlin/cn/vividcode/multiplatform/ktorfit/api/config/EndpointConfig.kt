package cn.vividcode.multiplatform.ktorfit.api.config

import cn.vividcode.multiplatform.ktorfit.annotation.KtorfitDsl
import io.ktor.client.plugins.*

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/10 19:10
 *
 * 文件介绍：EndpointConfig
 */
@KtorfitDsl
class EndpointConfig internal constructor() {
	
	var maxConnectionsPerRoute: Int = 100
	
	var keepAliveTime: Long = 5000L
	
	var pipelineMaxSize: Int = 20
	
	var connectTimeout: Long = 5000L
	
	var socketTimeout: Long = HttpTimeout.INFINITE_TIMEOUT_MS
	
	var connectAttempts: Int = 1
}