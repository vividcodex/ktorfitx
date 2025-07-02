package cn.ktorfitx.multiplatform.mock.config

import cn.ktorfitx.multiplatform.annotation.MockDsl
import io.ktor.client.plugins.logging.*

@MockDsl
class LogConfig internal constructor() {
	
	var level: LogLevel = LogLevel.HEADERS
	
	var logger: (String) -> Unit = ::println
}