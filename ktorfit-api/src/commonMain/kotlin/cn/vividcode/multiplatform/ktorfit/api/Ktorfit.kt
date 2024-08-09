package cn.vividcode.multiplatform.ktorfit.api

import cn.vividcode.multiplatform.ktorfit.api.builder.KtorfitBuilder
import cn.vividcode.multiplatform.ktorfit.api.builder.KtorfitBuilderImpl
import cn.vividcode.multiplatform.ktorfit.api.config.KtorConfig
import cn.vividcode.multiplatform.ktorfit.api.config.KtorfitConfig
import cn.vividcode.multiplatform.ktorfit.api.config.MockConfig
import cn.vividcode.multiplatform.ktorfit.api.mock.MockClient
import cn.vividcode.multiplatform.ktorfit.api.mock.plugin.MockCache
import cn.vividcode.multiplatform.ktorfit.api.mock.plugin.MockLogging
import cn.vividcode.multiplatform.ktorfit.scope.ApiScope
import cn.vividcode.multiplatform.ktorfit.scope.DefaultApiScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/9 23:52
 *
 * 文件介绍：Ktorfit
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Ktorfit<AS : ApiScope> internal constructor(
	val ktorfitConfig: KtorfitConfig,
	ktorConfig: KtorConfig,
	mockConfig: MockConfig,
	private val apiScope: AS
) {
	
	init {
		this.ktorfitConfig.check()
		ktorConfig.check()
	}
	
	@OptIn(ExperimentalSerializationApi::class)
	private val json = Json {
		this.prettyPrint = ktorConfig.jsonConfig.prettyPrint
		this.prettyPrintIndent = ktorConfig.jsonConfig.prettyPrintIndent
	}
	
	/**
	 * HttpClient
	 */
	val httpClient: HttpClient by lazy {
		HttpClient(CIO) {
			install(Logging) {
				this.logger = object : Logger {
					override fun log(message: String) {
						ktorConfig.handleLog?.invoke(format(message, ktorConfig.showApiScope))
					}
				}
				this.level = ktorConfig.logLevel
			}
			install(ContentNegotiation) {
				json(this@Ktorfit.json)
			}
			install(HttpCookies)
			engine {
				endpoint {
					this.connectTimeout = ktorConfig.connectTimeout
					this.socketTimeout = ktorConfig.socketTimeout
					this.keepAliveTime = ktorConfig.keepAliveTime
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
				this.baseUrl = ktorfitConfig.baseUrl
				this.logLevel = ktorConfig.logLevel
				this.handleLog = {
					ktorConfig.handleLog?.invoke(format(it, ktorConfig.showApiScope))
				}
				this.json = this@Ktorfit.json
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
 * DefaultApiScope 的 ktorfit 构造器
 */
fun ktorfit(builder: KtorfitBuilder.() -> Unit): Ktorfit<DefaultApiScope> {
	return KtorfitBuilderImpl(DefaultApiScope)
		.apply(builder)
		.build()
}

/**
 * 自定义 ApiScope 的 ktorfit 构造器
 */
fun <AS : ApiScope> ktorfit(apiScope: AS, builder: KtorfitBuilder.() -> Unit): Ktorfit<AS> {
	return KtorfitBuilderImpl(apiScope)
		.apply(builder)
		.build()
}