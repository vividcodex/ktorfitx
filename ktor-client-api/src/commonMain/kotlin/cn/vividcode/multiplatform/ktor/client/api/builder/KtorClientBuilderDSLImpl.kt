package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.KtorClient

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/13 上午2:20
 *
 * 介绍：KtorClientBuilderDSLImpl
 */
internal class KtorClientBuilderDSLImpl : KtorClientBuilderDSL {
	
	private var domain: String = ""
	private var connectTimeout: Long = 5000L
	private var socketTimeout: Long = Long.MAX_VALUE
	private var handleLog: (message: String) -> Unit = {}
	private var getToken: (() -> String)? = null
	
	override fun domain(domain: String) {
		this.domain = domain
	}
	
	override fun connectTimeout(connectTimeout: Long) {
		this.connectTimeout = connectTimeout
	}
	
	override fun socketTimeout(socketTimeout: Long) {
		this.socketTimeout = socketTimeout
	}
	
	override fun handleLog(handleLog: (message: String) -> Unit) {
		this.handleLog = handleLog
	}
	
	override fun getToken(getToken: () -> String) {
		this.getToken = getToken
	}
	
	fun build(): KtorClient {
		check(this.domain.isNotEmpty()) { "ktorClient 的 domain 还没有配置" }
		checkNotNull(this.getToken) { "ktorClient 的 getToken 还没有配置" }
		return KtorClient(this.domain, this.connectTimeout, this.socketTimeout, this.handleLog, this.getToken!!)
	}
}