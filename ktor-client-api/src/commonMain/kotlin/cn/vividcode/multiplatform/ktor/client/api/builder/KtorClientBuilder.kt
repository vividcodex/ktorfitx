package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MocksConfig
import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MocksConfigImpl
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.api.config.MockConfig
import io.ktor.client.plugins.logging.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/28 下午3:37
 *
 * 介绍：KtorClientBuilder
 */
@KtorBuilderDsl
sealed interface KtorClientBuilder<AS : ApiScope> {
	
	/**
	 * baseUrl
	 */
	fun baseUrl(baseUrl: String): KtorClientBuilder<AS>
	
	/**
	 * baseUrl
	 */
	fun baseUrl(
		host: String = "localhost",
		port: Int = 80,
		safe: Boolean = false,
		prefix: String = ""
	): KtorClientBuilder<AS>
	
	/**
	 * token
	 */
	fun token(token: () -> String): KtorClientBuilder<AS>
	
	/**
	 * mocks
	 */
	fun mocks(block: MocksConfig.() -> Unit): KtorClientBuilder<AS>
	
	/**
	 * connectTimeout
	 */
	fun connectTimeout(connectTimeout: Long): KtorClientBuilder<AS>
	
	/**
	 * socketTimeout
	 */
	fun socketTimeout(socketTimeout: Long): KtorClientBuilder<AS>
	
	/**
	 * keepAliveTime
	 */
	fun keepAliveTime(keepAliveTime: Long): KtorClientBuilder<AS>
	
	/**
	 * logLevel
	 */
	fun logLevel(logLevel: LogLevel): KtorClientBuilder<AS>
	
	/**
	 * handleLog
	 */
	fun handleLog(handleLog: (String) -> Unit): KtorClientBuilder<AS>
	
	/**
	 * build
	 */
	fun build(): KtorClient<AS>
}

internal class KtorClientBuilderImpl<AS : ApiScope> : KtorClientBuilder<AS> {
	
	private val ktorConfig by lazy { KtorConfig() }
	private val httpConfig by lazy { HttpConfig() }
	private val mockConfig by lazy { MockConfig() }
	
	override fun baseUrl(baseUrl: String): KtorClientBuilder<AS> {
		this.ktorConfig.baseUrl = baseUrl
		return this
	}
	
	override fun baseUrl(host: String, port: Int, safe: Boolean, prefix: String): KtorClientBuilder<AS> {
		val baseUrl = buildString {
			append(if (safe) "https://" else "http://")
			append(host)
			append(':')
			append(port)
			append(if (prefix.startsWith('/')) prefix else "/$prefix")
		}
		this.ktorConfig.baseUrl = baseUrl
		return this
	}
	
	override fun token(token: () -> String): KtorClientBuilder<AS> {
		this.ktorConfig.token = token
		return this
	}
	
	override fun mocks(block: MocksConfig.() -> Unit): KtorClientBuilder<AS> {
		val groupMocksMap = MocksConfigImpl().apply(block).groupMocksMap
		this.mockConfig.addGroupMocksMap(groupMocksMap)
		return this
	}
	
	override fun connectTimeout(connectTimeout: Long): KtorClientBuilder<AS> {
		this.httpConfig.connectTimeout = connectTimeout
		return this
	}
	
	override fun socketTimeout(socketTimeout: Long): KtorClientBuilder<AS> {
		this.httpConfig.socketTimeout = socketTimeout
		return this
	}
	
	override fun keepAliveTime(keepAliveTime: Long): KtorClientBuilder<AS> {
		this.httpConfig.keepAliveTime = keepAliveTime
		return this
	}
	
	override fun logLevel(logLevel: LogLevel): KtorClientBuilder<AS> {
		this.httpConfig.logLevel = logLevel
		return this
	}
	
	override fun handleLog(handleLog: (String) -> Unit): KtorClientBuilder<AS> {
		this.httpConfig.handleLog = handleLog
		return this
	}
	
	override fun build(): KtorClient<AS> = KtorClient(ktorConfig, httpConfig, mockConfig)
}