package cn.vividcode.multiplatform.ktorfitx.api.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl
import cn.vividcode.multiplatform.ktorfitx.api.Ktorfit
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/10 20:40
 *
 * 文件介绍：KtorfitConfig
 */
@KtorfitDsl
class KtorfitConfig internal constructor() {
	
	var baseUrl: String = ""
	
	var engineFactory: HttpClientEngineFactory<*>? = null
	
	var token: (() -> String)? = null
		private set
	
	internal var timeout: (HttpTimeoutConfig.() -> Unit)? = null
		private set
	
	internal var log: LogConfig? = null
		private set
	
	internal var json: Json? = null
		private set
	
	internal var apiScope: ApiScopeConfig? = null
		private set
	
	fun token(token: () -> String) {
		this.token = token
	}
	
	fun timeout(config: HttpTimeoutConfig.() -> Unit) {
		this.timeout = config
	}
	
	fun log(config: LogConfig.() -> Unit) {
		this.log = LogConfig().apply(config)
	}
	
	fun json(config: JsonBuilder.() -> Unit) {
		this.json = Json { this.config() }
	}
	
	fun apiScope(config: ApiScopeConfig.() -> Unit) {
		this.apiScope = ApiScopeConfig().apply(config)
	}
	
	fun <AS : ApiScope> build(apiScope: AS): Ktorfit<AS> {
		check(baseUrl.isNotBlank()) { "请设置 baseUrl" }
		this.engineFactory = this.engineFactory ?: CIO
		this.token = this.token ?: { "" }
		this.log = this.log ?: LogConfig()
		this.json = this.json ?: Json
		this.apiScope = this.apiScope ?: ApiScopeConfig()
		return Ktorfit(this, apiScope)
	}
}