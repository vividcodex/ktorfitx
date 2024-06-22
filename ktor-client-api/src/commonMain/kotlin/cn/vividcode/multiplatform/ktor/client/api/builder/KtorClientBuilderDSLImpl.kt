package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import io.ktor.client.plugins.logging.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/13 上午2:20
 *
 * 介绍：KtorClientBuilderDSLImpl
 */
internal class KtorClientBuilderDSLImpl<AS : ApiScope> : KtorClientBuilderDSL {
	
	private val ktorConfig = KtorConfig()
	private val httpConfig = HttpConfig()
	
	override fun domain(domain: String) {
		this.ktorConfig.domain = domain
	}
	
	override fun getToken(getToken: () -> String) {
		this.ktorConfig.getToken = getToken
	}
	
	override fun connectTimeout(connectTimeout: Long) {
		this.httpConfig.connectTimeout = connectTimeout
	}
	
	override fun socketTimeout(socketTimeout: Long) {
		this.httpConfig.socketTimeout = socketTimeout
	}
	
	override fun keepAliveTime(keepAliveTime: Long) {
		this.httpConfig.keepAliveTime = keepAliveTime
	}
	
	override fun logLevel(logLevel: LogLevel) {
		this.httpConfig.logLevel = logLevel
	}
	
	override fun handleLog(handleLog: (message: String) -> Unit) {
		this.httpConfig.handleLog = handleLog
	}
	
	fun build(): KtorClient<AS> {
		this.ktorConfig.check()
		return KtorClient(this.ktorConfig, this.httpConfig)
	}
}