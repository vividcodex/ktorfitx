package cn.ktorfitx.multiplatform.mock.config

import cn.ktorfitx.multiplatform.annotation.MockDsl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

@MockDsl
class MockClientConfig internal constructor() {
	
	var json: Json? = null
		private set
	
	var log: LogConfig? = null
		private set
	
	fun json(
		from: Json = Json.Default,
		builderAction: JsonBuilder.() -> Unit
	) {
		this.json = Json(from, builderAction)
	}
	
	fun log(builder: LogConfig.() -> Unit) {
		this.log = LogConfig().apply(builder)
	}
	
	fun build(): MockClientConfig {
		this.json = this.json ?: Json.Default
		this.log = this.log ?: LogConfig()
		return this
	}
}