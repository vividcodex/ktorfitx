package cn.vividcode.multiplatform.ktor.client.api

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilder
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDsl
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDslImpl
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderImpl
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.api.config.MockConfig
import cn.vividcode.multiplatform.ktor.client.api.mock.MockClient
import cn.vividcode.multiplatform.ktor.client.api.mock.plugin.MockCache
import cn.vividcode.multiplatform.ktor.client.api.mock.plugin.MockLogger
import cn.vividcode.multiplatform.ktor.client.api.mock.plugin.MockLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/9 下午11:52
 *
 * 介绍：KtorClient
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class KtorClient<AS : ApiScope> internal constructor(
	val ktorConfig: KtorConfig,
	httpConfig: HttpConfig,
	mockConfig: MockConfig
) {
	
	init {
		this.ktorConfig.check()
		httpConfig.check()
	}
	
	companion object {
		
		/**
		 * ktorClient 的构造器
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
				val json = Json {
					prettyPrint = httpConfig.jsonPrettyPrint
				}
				json(json)
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
	
	val mockClient: MockClient by lazy {
		MockClient {
			install(MockLogging) {
				this.baseUrl = ktorConfig.baseUrl
				this.logger = MockLogger {
					httpConfig.handleLog(it)
				}
				this.logLevel = httpConfig.logLevel
			}
			install(MockCache) {
				this.groupMocksMap = mockConfig.groupMocksMap
			}
		}
	}
}

/**
 * ktorClient 的Dsl构造器
 */
fun <AS : ApiScope> ktorClient(builder: KtorClientBuilderDsl.() -> Unit): KtorClient<AS> {
	return KtorClientBuilderDslImpl<AS>()
		.apply(builder)
		.build()
}