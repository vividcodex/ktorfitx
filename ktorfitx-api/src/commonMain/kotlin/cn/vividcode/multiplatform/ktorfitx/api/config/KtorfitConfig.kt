package cn.vividcode.multiplatform.ktorfitx.api.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl
import cn.vividcode.multiplatform.ktorfitx.api.Ktorfit
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockClient
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import io.ktor.client.*
import io.ktor.client.engine.*

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
	
	var token: (() -> String)? = null
		private set
	
	var httpClient: HttpClient? = null
		private set
	
	var mockClient: MockClient? = null
		private set
	
	fun <T : HttpClientEngineConfig> httpClient(
		engineFactory: HttpClientEngineFactory<T>,
		block: HttpClientConfig<T>.() -> Unit = {}
	) {
		this.httpClient = HttpClient(engineFactory, block)
	}
	
	fun mockClient(builder: MockClientConfig.() -> Unit) {
		val config = MockClientConfig().apply(builder).build()
		this.mockClient = MockClient(config.log!!, config.json!!)
	}
	
	internal var apiScope: ApiScopeConfig? = null
		private set
	
	fun token(token: () -> String) {
		this.token = token
	}
	
	fun apiScope(config: ApiScopeConfig.() -> Unit) {
		this.apiScope = ApiScopeConfig().apply(config)
	}
	
	fun <AS : ApiScope> build(apiScope: AS): Ktorfit<AS> {
		this.token = this.token ?: { "" }
		this.apiScope = this.apiScope ?: ApiScopeConfig()
		this.httpClient = this.httpClient ?: HttpClient()
		this.mockClient = this.mockClient ?: MockClient()
		return Ktorfit(this, apiScope)
	}
}