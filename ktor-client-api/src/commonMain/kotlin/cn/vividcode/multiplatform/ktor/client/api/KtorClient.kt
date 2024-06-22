package cn.vividcode.multiplatform.ktor.client.api

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilder
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDSL
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDSLImpl
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderImpl
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/9 下午11:52
 *
 * 介绍：KtorClient
 */
class KtorClient<AS : ApiScope> internal constructor(
	val ktorConfig: KtorConfig,
	private val httpConfig: HttpConfig,
) {
	
	companion object {
		
		/**
		 * KtorClientBuilder
		 */
		fun <AS : ApiScope> builder(): KtorClientBuilder<AS> = KtorClientBuilderImpl()
	}
	
	/**
	 * HttpClient
	 */
	val httpClient: HttpClient by lazy {
		HttpClient(CIO) {
			install(Logging) {
				this.logger = object : Logger {
					override fun log(message: String) {
						httpConfig.handleLog(message)
					}
				}
				this.level = httpConfig.logLevel
			}
			install(ContentNegotiation) {
				json()
			}
			install(HttpCookies)
			engine {
				endpoint {
					this.connectTimeout = httpConfig.connectTimeout
					this.socketTimeout = httpConfig.socketTimeout
					this.keepAliveTime = httpConfig.keepAliveTime
				}
			}
		}
	}
}

/**
 * KtorClient DSL
 */
fun <AS : ApiScope> ktorClient(builder: KtorClientBuilderDSL.() -> Unit): KtorClient<AS> {
	return KtorClientBuilderDSLImpl<AS>().apply(builder).build()
}