package cn.ktorfitx.multiplatform.core.config

import cn.ktorfitx.multiplatform.annotation.KtorfitDsl
import cn.ktorfitx.multiplatform.core.Ktorfit
import cn.ktorfitx.multiplatform.core.scope.ApiScope
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
	
	fun token(token: () -> String?) {
		this.token = token
	}
	
	fun <AS : ApiScope> build(): Ktorfit<AS> {
		this.token = this.token ?: { null }
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
		return Ktorfit(this)
	}
	
	private class HttpClientBlock(
		val engine: HttpClientEngineFactory<*>,
		val block: HttpClientConfig<*>.() -> Unit
	)
}