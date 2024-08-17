package cn.vividcode.multiplatform.ktorfitx.api.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl
import cn.vividcode.multiplatform.ktorfitx.api.Ktorfit
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope

/**
 * 项目名称：vividcode-multiplatform-ktorfit
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
	
	var token: (() -> String)? = null
		private set
	
	internal var endpoint: EndpointConfig? = null
		private set
	
	internal var log: LogConfig? = null
		private set
	
	internal var json: JsonConfig? = null
		private set
	
	internal var apiScope: ApiScopeConfig? = null
		private set
	
	fun token(token: () -> String) {
		this.token = token
	}
	
	fun endpoint(config: EndpointConfig.() -> Unit) {
		this.endpoint = EndpointConfig().apply(config)
	}
	
	fun log(config: LogConfig.() -> Unit) {
		this.log = LogConfig().apply(config)
	}
	
	fun json(config: JsonConfig.() -> Unit) {
		this.json = JsonConfig().apply(config)
	}
	
	fun apiScope(config: ApiScopeConfig.() -> Unit) {
		this.apiScope = ApiScopeConfig().apply(config)
	}
	
	fun <AS : ApiScope> build(apiScope: AS): Ktorfit<AS> {
		check(baseUrl.isNotBlank()) { "请设置 baseUrl" }
		this.token = this.token ?: { "" }
		this.endpoint = this.endpoint ?: EndpointConfig()
		this.log = this.log ?: LogConfig()
		this.json = this.json ?: JsonConfig()
		this.apiScope = this.apiScope ?: ApiScopeConfig()
		return Ktorfit(this, apiScope)
	}
}