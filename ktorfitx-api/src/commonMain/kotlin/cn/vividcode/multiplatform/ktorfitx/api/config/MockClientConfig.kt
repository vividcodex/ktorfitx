package cn.vividcode.multiplatform.ktorfitx.api.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

@KtorfitDsl
class MockClientConfig internal constructor() {
	
	var json: Json? = null
		private set
	
	fun json(builder: JsonBuilder.() -> Unit) {
		this.json = Json(builderAction = builder)
	}
	
	var log: LogConfig? = null
		private set
	
	fun log(builder: LogConfig.() -> Unit) {
		this.log = LogConfig().apply(builder)
	}
	
	fun build(): MockClientConfig {
		if (json == null) {
			json = Json
		}
		if (log == null) {
			log = LogConfig()
		}
		return this
	}
}