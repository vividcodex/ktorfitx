package cn.vividcode.multiplatform.ktorfitx.mock.config

import cn.vividcode.multiplatform.ktorfitx.annotation.MockDsl
import io.ktor.client.plugins.logging.*

@MockDsl
class LogConfig internal constructor() {
	
	var level: LogLevel = LogLevel.HEADERS
	
	var logger: (String) -> Unit = ::println
}