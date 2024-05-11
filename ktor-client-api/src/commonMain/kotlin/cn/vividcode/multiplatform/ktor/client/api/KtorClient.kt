package cn.vividcode.multiplatform.ktor.client.api

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
class KtorClient private constructor(
	val domain: String,
	private val connectTimeout: Long,
	private val socketTimeout: Long,
	private val handleLog: (message: String) -> Unit,
	val getToken: () -> String
) {
	
	companion object {
		
		fun builder(): Builder = Builder()
	}
	
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
	
	class Builder {
		
		private var domain: String = ""
		private var connectTimeout: Long = 5000L
		private var socketTimeout: Long = Long.MAX_VALUE
		private var handleLog: (message: String) -> Unit = {}
		private var getToken: (() -> String)? = null
		
		fun domain(domain: String): Builder = apply {
			this.domain = domain
		}
		
		fun connectTimeout(connectTimeout: Long): Builder = apply {
			this.connectTimeout = connectTimeout
		}
		
		fun socketTimeout(socketTimeout: Long): Builder = apply {
			this.socketTimeout = socketTimeout
		}
		
		fun handleLog(handleLog: (message: String) -> Unit): Builder = apply {
			this.handleLog = handleLog
		}
		
		fun getToken(getToken: () -> String): Builder = apply {
			this.getToken = getToken
		}
		
		fun build(): KtorClient {
			check(this.domain.isNotEmpty()) { "ktorClient 的 domain 还没有配置" }
			checkNotNull(this.getToken) { "ktorClient 的 getToken 还没有配置" }
			return KtorClient(this.domain, this.connectTimeout, this.socketTimeout, this.handleLog, this.getToken!!)
		}
	}
}