package cn.vividcode.multiplatform.ktorfitx.core.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl
import cn.vividcode.multiplatform.ktorfitx.core.Ktorfit
import cn.vividcode.multiplatform.ktorfitx.core.scope.ApiScope
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*

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
	
	var baseUrl: String? = null
	
	var token: (() -> String?)? = null
		private set
	
	var httpClient: HttpClient? = null
		private set
	
	private var httpClientBlock: HttpClientBlock? = null
	
	fun <T : HttpClientEngineConfig> httpClient(
		engineFactory: HttpClientEngineFactory<T>,
		block: HttpClientConfig<T>.() -> Unit = {}
	) {
		@Suppress("UNCHECKED_CAST")
		this.httpClientBlock = HttpClientBlock(engineFactory, block as (HttpClientConfig<*>.() -> Unit))
	}
	
	internal var apiScope: ApiScopeConfig? = null
		private set
	
	fun token(token: () -> String?) {
		this.token = token
	}
	
	fun apiScope(config: ApiScopeConfig.() -> Unit) {
		this.apiScope = ApiScopeConfig().apply(config)
	}
	
	fun <AS : ApiScope> build(apiScope: AS): Ktorfit<AS> {
		this.token = this.token ?: { null }
		this.apiScope = this.apiScope ?: ApiScopeConfig()
		this.httpClient = if (httpClientBlock == null) HttpClient() else with(httpClientBlock!!) {
			HttpClient(engine) {
				defaultRequest {
					if (baseUrl != null) {
						url(baseUrl!!)
					}
				}
				block()
			}
		}
		return Ktorfit(this, apiScope)
	}
	
	private class HttpClientBlock(
		val engine: HttpClientEngineFactory<*>,
		val block: HttpClientConfig<*>.() -> Unit
	)
}