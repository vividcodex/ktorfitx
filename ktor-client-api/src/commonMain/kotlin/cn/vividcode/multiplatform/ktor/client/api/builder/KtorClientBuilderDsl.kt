package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.api.mock.MocksDsl
import io.ktor.client.plugins.logging.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/28 下午4:01
 *
 * 介绍：KtorClientBuilderDSL
 */
@KtorBuilderDsl
sealed interface KtorClientBuilderDsl {
	
	/**
	 * baseUrl
	 */
	var baseUrl: String
	
	/**
	 * baseUrl
	 */
	fun baseUrl(block: BaseUrlBuilderDsl.() -> Unit)
	
	/**
	 * token
	 */
	fun token(token: () -> String)
	
	/**
	 * mocks
	 */
	fun mocks(block: MocksDsl.() -> Unit)
	
	/**
	 * connectTimeout
	 */
	var connectTimeout: Long
	
	/**
	 * socketTimeout
	 */
	var socketTimeout: Long
	
	/**
	 * keepAliveTime
	 */
	var keepAliveTime: Long
	
	/**
	 * logLevel
	 */
	var logLevel: LogLevel
	
	/**
	 * handleLog
	 */
	fun handleLog(handleLog: (String) -> Unit)
}

internal class KtorClientBuilderDslImpl<AS : ApiScope> : KtorClientBuilderDsl {
	
	private val ktorConfig by lazy { KtorConfig() }
	private val httpConfig by lazy { HttpConfig() }
	private val mockBuilderDelegate by lazy { MockBuilderDelegate() }
	
	override var baseUrl: String = ktorConfig.baseUrl
		set(value) {
			field = value
			this.ktorConfig.baseUrl = baseUrl
		}
	
	override fun baseUrl(block: BaseUrlBuilderDsl.() -> Unit) {
		this.ktorConfig.baseUrl = BaseUrlBuilderDslImpl()
			.apply(block)
			.build()
	}
	
	override fun token(token: () -> String) {
		this.ktorConfig.token = token
	}
	
	override fun mocks(block: MocksDsl.() -> Unit) {
		this.mockBuilderDelegate.mocks(this.ktorConfig.groupMocksMap, block)
	}
	
	override var connectTimeout: Long = httpConfig.connectTimeout
		set(value) {
			field = value
			this.httpConfig.connectTimeout = value
		}
	
	override var socketTimeout: Long = httpConfig.socketTimeout
		set(value) {
			field = value
			this.httpConfig.connectTimeout = value
		}
	
	override var keepAliveTime: Long = httpConfig.keepAliveTime
		set(value) {
			field = value
			this.httpConfig.keepAliveTime = value
		}
	
	override var logLevel: LogLevel = httpConfig.logLevel
		set(value) {
			field = value
			this.httpConfig.logLevel = value
		}
	
	override fun handleLog(handleLog: (String) -> Unit) {
		this.httpConfig.handleLog = handleLog
	}
	
	internal fun build(): KtorClient<AS> {
		return KtorClient(this.ktorConfig, this.httpConfig)
	}
}

@KtorBuilderDsl
sealed interface BaseUrlBuilderDsl {
	
	var host: String
	
	var port: Int
	
	var safe: Boolean
	
	var prefix: String
}

private class BaseUrlBuilderDslImpl : BaseUrlBuilderDsl {
	
	override var host: String = "localhost"
	
	override var port: Int = 80
	
	override var safe: Boolean = false
	
	override var prefix: String = ""
	
	fun build(): String = buildString {
		append(if (safe) "https://" else "http://")
		append(host)
		append(':')
		append(port)
		append(if (prefix.startsWith('/')) prefix else "/$prefix")
	}
}