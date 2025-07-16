package cn.ktorfitx.multiplatform.core.config

import cn.ktorfitx.multiplatform.core.Ktorfitx
import cn.ktorfitx.multiplatform.core.annotation.KtorfitDsl
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*

@KtorfitDsl
class KtorfitxConfig internal constructor() {
	
	var baseUrl: String? = null
	
	var token: (suspend () -> String?)? = null
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
	
	fun token(token: suspend () -> String?) {
		this.token = token
	}
	
	fun <AS : Any> build(): Ktorfitx<AS> {
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
		return Ktorfitx(this)
	}
	
	private class HttpClientBlock(
		val engine: HttpClientEngineFactory<*>,
		val block: HttpClientConfig<*>.() -> Unit
	)
}