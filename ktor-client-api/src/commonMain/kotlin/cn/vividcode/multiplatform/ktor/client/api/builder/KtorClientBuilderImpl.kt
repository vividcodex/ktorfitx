package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import io.ktor.client.plugins.logging.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/13 上午2:19
 *
 * 介绍：KtorClientBuilderImpl
 */
internal class KtorClientBuilderImpl : KtorClientBuilder {
	
	private val ktorConfig = KtorConfig()
	private val httpConfig = HttpConfig()
	
	override fun domain(domain: String): KtorClientBuilder {
		this.ktorConfig.domain = domain
		return this
	}
	
	override fun getToken(getToken: () -> String): KtorClientBuilder {
		this.ktorConfig.getToken = getToken
		return this
	}
	
	override fun connectTimeout(connectTimeout: Long): KtorClientBuilder {
		this.httpConfig.connectTimeout = connectTimeout
		return this
	}
	
	override fun socketTimeout(socketTimeout: Long): KtorClientBuilder {
		this.httpConfig.socketTimeout = socketTimeout
		return this
	}
	
	override fun keepAliveTime(keepAliveTime: Long): KtorClientBuilder {
		this.httpConfig.keepAliveTime = keepAliveTime
		return this
	}
	
	override fun logLevel(logLevel: LogLevel): KtorClientBuilder {
		this.httpConfig.logLevel = logLevel
		return this
	}
	
	override fun handleLog(handleLog: (message: String) -> Unit): KtorClientBuilder {
		this.httpConfig.handleLog = handleLog
		return this
	}
	
	override fun build(): KtorClient {
		this.ktorConfig.check()
		return KtorClient(this.ktorConfig, this.httpConfig)
	}
}