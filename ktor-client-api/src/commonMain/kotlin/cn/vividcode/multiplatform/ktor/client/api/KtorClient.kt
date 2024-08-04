package cn.vividcode.multiplatform.ktor.client.api

import cn.vividcode.multiplatform.ktor.client.annotation.ApiScope
import cn.vividcode.multiplatform.ktor.client.annotation.DefaultApiScope
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilder
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDsl
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDslImpl
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderImpl
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.api.config.MockConfig
import cn.vividcode.multiplatform.ktor.client.api.mock.MockClient
import cn.vividcode.multiplatform.ktor.client.api.mock.plugin.MockCache
import cn.vividcode.multiplatform.ktor.client.api.mock.plugin.MockLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
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
	mockConfig: MockConfig,
	private val apiScope: AS
) {
	
	init {
		this.ktorConfig.check()
		httpConfig.check()
	}
	
	companion object {
		
		fun builder(): KtorClientBuilder<DefaultApiScope> = KtorClientBuilderImpl(DefaultApiScope)
		
		/**
		 * ktorClient 的构造器
		 */
		fun <AS : ApiScope> builder(apiScope: AS): KtorClientBuilder<AS> = KtorClientBuilderImpl(apiScope)
	}
	
	@OptIn(ExperimentalSerializationApi::class)
	private val json = Json {
		this.prettyPrint = httpConfig.jsonConfig.prettyPrint
		this.prettyPrintIndent = httpConfig.jsonConfig.prettyPrintIndent
	}
	
	/**
	 * HttpClient
	 */
	val httpClient: HttpClient by lazy {
		HttpClient(CIO) {
			install(Logging) {
				this.logger = object : Logger {
					override fun log(message: String) {
						httpConfig.handleLog?.invoke(format(message, httpConfig.showApiScope))
					}
				}
				this.level = httpConfig.logLevel
			}
			install(ContentNegotiation) {
				json(this@KtorClient.json)
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
	
	/**
	 * MockClient
	 */
	val mockClient: MockClient by lazy {
		MockClient {
			install(MockLogging) {
				this.baseUrl = ktorConfig.baseUrl
				this.logLevel = httpConfig.logLevel
				this.handleLog = {
					httpConfig.handleLog?.invoke(format(it, httpConfig.showApiScope))
				}
				this.json = this@KtorClient.json
			}
			install(MockCache) {
				this.groupMocksMap = mockConfig.groupMocksMap
			}
		}
	}
	
	private fun format(message: String, showApiScope: Boolean): String {
		return message + if (showApiScope) " <[$apiScope]>" else ""
	}
}

/**
 * DefaultApiScope 的 ktorClient 构造器
 */
fun ktorClient(builder: KtorClientBuilderDsl.() -> Unit): KtorClient<DefaultApiScope> {
	return KtorClientBuilderDslImpl(DefaultApiScope)
		.apply(builder)
		.build()
}

/**
 * 自定义 ApiScope 的 ktorClient 构造器
 */
fun <AS : ApiScope> ktorClient(apiScope: AS, builder: KtorClientBuilderDsl.() -> Unit): KtorClient<AS> {
	return KtorClientBuilderDslImpl(apiScope)
		.apply(builder)
		.build()
}