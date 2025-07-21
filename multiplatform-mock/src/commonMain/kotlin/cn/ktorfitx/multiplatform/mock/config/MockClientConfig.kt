package cn.ktorfitx.multiplatform.mock.config

import cn.ktorfitx.multiplatform.annotation.MockDsl
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json

@MockDsl
class MockClientConfig internal constructor() {
	
	var json: Json? = null
		private set
	
	var log: LogConfig? = null
		private set
	
	var format: StringFormat? = null
	
	fun log(builder: LogConfig.() -> Unit) {
		this.log = LogConfig().apply(builder)
	}
	
	fun build(): MockClientConfig {
		this.log = this.log ?: LogConfig()
		return this
	}
}