package cn.vividcode.multiplatform.ktorfitx.api

import cn.vividcode.multiplatform.ktorfitx.api.config.KtorfitConfig
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockClient
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import cn.vividcode.multiplatform.ktorfitx.api.scope.DefaultApiScope
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

/**
 * 项目名称：ktorfitx
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
	private val apiScope: AS,
) {
	
	/**
	 * HttpClient
	 */
	val httpClient: HttpClient by lazy {
		HttpClient(this.ktorfit.engineFactory!!) {
			install(Logging) {
				this.logger = KtorfitLogger()
				this.level = ktorfit.log!!.level
			}
			install(ContentNegotiation) {
				json(ktorfit.json!!)
			}
			install(HttpCookies)
			install(HttpTimeout) {
				ktorfit.timeout?.let {
					connectTimeoutMillis = it.connectTimeoutMillis
					requestTimeoutMillis = it.requestTimeoutMillis
					socketTimeoutMillis = it.socketTimeoutMillis
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
		MockClient(ktorfit.log!!, this.ktorfit.json!!)
	}
}

/**
 * ktorfit
 */
fun ktorfit(
	config: KtorfitConfig.() -> Unit,
): Ktorfit<DefaultApiScope> = KtorfitConfig()
	.apply(config)
	.build(DefaultApiScope)

/**
 * ktorfit
 */
fun <AS : ApiScope> ktorfit(
	apiScope: AS,
	config: KtorfitConfig.() -> Unit,
): Ktorfit<AS> = KtorfitConfig()
	.apply(config)
	.build(apiScope)