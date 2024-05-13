package cn.vividcode.multiplatform.ktor.client.api

import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilder
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDSL
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderDSLImpl
import cn.vividcode.multiplatform.ktor.client.api.builder.KtorClientBuilderImpl
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
class KtorClient internal constructor(
	val domain: String,
	private val connectTimeout: Long,
	private val socketTimeout: Long,
	private val handleLog: (message: String) -> Unit,
	val getToken: () -> String
) {
	
	companion object {
		
		/**
		 * 构建
		 */
		fun builder(): KtorClientBuilder = KtorClientBuilderImpl()
	}
	
	/**
	 * HttpClient
	 */
	val httpClient: HttpClient by lazy {
		HttpClient(CIO) {
			install(Logging) {
				this.logger = object : Logger {
					override fun log(message: String) {
						handleLog(message)
					}
				}
				this.level = LogLevel.BODY
			}
			install(ContentNegotiation) {
				json()
			}
			install(HttpCookies)
			engine {
				endpoint {
					this.connectTimeout = this@KtorClient.connectTimeout
					this.socketTimeout = this@KtorClient.socketTimeout
				}
			}
		}
	}
}

/**
 * KtorClient DSL
 */
fun ktorClient(builder: KtorClientBuilderDSL.() -> Unit): KtorClient {
	return KtorClientBuilderDSLImpl().apply(builder).build()
}