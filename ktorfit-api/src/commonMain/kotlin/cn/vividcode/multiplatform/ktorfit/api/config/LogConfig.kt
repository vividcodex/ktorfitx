package cn.vividcode.multiplatform.ktorfit.api.config

import cn.vividcode.multiplatform.ktorfit.annotation.KtorfitDsl
import io.ktor.client.plugins.logging.*

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/10 20:38
 *
 * 文件介绍：LogConfig
 */
@KtorfitDsl
class LogConfig internal constructor() {
	
	var level: LogLevel = LogLevel.HEADERS
	
	var logger: (String) -> Unit = ::println
}