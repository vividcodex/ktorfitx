package cn.vividcode.multiplatform.ktor.client.api.impl

import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.KtorClientBuilder

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
	
	private var domain: String = ""
	private var connectTimeout: Long = 5000L
	private var socketTimeout: Long = Long.MAX_VALUE
	private var handleLog: (message: String) -> Unit = {}
	private var getToken: (() -> String)? = null
	
	override fun domain(domain: String): KtorClientBuilder {
		this.domain = domain
		return this
	}
	
	override fun connectTimeout(connectTimeout: Long): KtorClientBuilder {
		this.connectTimeout = connectTimeout
		return this
	}
	
	override fun socketTimeout(socketTimeout: Long): KtorClientBuilder {
		this.socketTimeout = socketTimeout
		return this
	}
	
	override fun handleLog(handleLog: (message: String) -> Unit): KtorClientBuilder {
		this.handleLog = handleLog
		return this
	}
	
	override fun getToken(getToken: () -> String): KtorClientBuilder {
		this.getToken = getToken
		return this
	}
	
	override fun build(): KtorClient {
		check(this.domain.isNotEmpty()) { "ktorClient 的 domain 还没有配置" }
		checkNotNull(this.getToken) { "ktorClient 的 getToken 还没有配置" }
		return KtorClient(this.domain, this.connectTimeout, this.socketTimeout, this.handleLog, this.getToken!!)
	}
}