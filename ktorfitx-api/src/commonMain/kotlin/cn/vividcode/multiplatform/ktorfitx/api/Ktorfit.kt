package cn.vividcode.multiplatform.ktorfitx.api

import cn.vividcode.multiplatform.ktorfitx.api.config.KtorfitConfig
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockClient
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import cn.vividcode.multiplatform.ktorfitx.api.scope.DefaultApiScope
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
	val ktorfit: KtorfitConfig,
	private val apiScope: AS
) {
	
	@OptIn(ExperimentalSerializationApi::class)
	private val globalJson = Json {
		this.prettyPrint = ktorfit.json!!.prettyPrint
		this.prettyPrintIndent = ktorfit.json!!.prettyPrintIndent
		this.ignoreUnknownKeys = ktorfit.json!!.ignoreUnknownKeys
	}
	
	/**
	 * HttpClient
	 */
	val httpClient: HttpClient by lazy {
		HttpClient(CIO) {
			install(Logging) {
				this.logger = KtorfitLogger()
				this.level = ktorfit.log!!.level
			}
			install(ContentNegotiation) {
				json(globalJson)
			}
			install(HttpCookies)
			engine {
				endpoint {
					ktorfit.endpoint!!.let {
						this.maxConnectionsPerRoute = it.maxConnectionsPerRoute
						this.keepAliveTime = it.keepAliveTime
						this.pipelineMaxSize = it.pipelineMaxSize
						this.connectTimeout = it.connectTimeout
						this.socketTimeout = it.socketTimeout
						this.connectAttempts = it.connectAttempts
					}
				}
			}
		}
	}
	
	private inner class KtorfitLogger : Logger {
		
		override fun log(message: String) {
			ktorfit.log!!.logger(formatMessage(message))
		}
		
		private fun formatMessage(message: String): String {
			return message + if (ktorfit.apiScope!!.printName) " [${apiScope.name}]" else ""
		}
	}
	
	/**
	 * MockClient
	 */
	val mockClient: MockClient by lazy {
		MockClient(ktorfit.log!!, globalJson)
	}
}

/**
 * ktorfit
 */
fun ktorfit(
	config: KtorfitConfig.() -> Unit
): Ktorfit<DefaultApiScope> = KtorfitConfig()
	.apply(config)
	.build(DefaultApiScope)

/**
 * ktorfit
 */
fun <AS : ApiScope> ktorfit(
	apiScope: AS,
	config: KtorfitConfig.() -> Unit
): Ktorfit<AS> = KtorfitConfig()
	.apply(config)
	.build(apiScope)