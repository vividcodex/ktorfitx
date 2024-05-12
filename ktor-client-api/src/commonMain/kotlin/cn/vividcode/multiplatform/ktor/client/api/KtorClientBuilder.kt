package cn.vividcode.multiplatform.ktor.client.api

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/13 上午2:19
 *
 * 介绍：KtorClientBuilder
 */
interface KtorClientBuilder {
	
	/**
	 * 域名
	 */
	fun domain(domain: String): KtorClientBuilder
	
	/**
	 * 连接超时时长 (ms)
	 */
	fun connectTimeout(connectTimeout: Long): KtorClientBuilder
	
	/**
	 * Socket 超时时长 (ms)
	 */
	fun socketTimeout(socketTimeout: Long): KtorClientBuilder
	
	/**
	 * 日志处理
	 */
	fun handleLog(handleLog: (message: String) -> Unit): KtorClientBuilder
	
	/**
	 * 获取Token
	 */
	fun getToken(getToken: () -> String): KtorClientBuilder
	
	/**
	 * 构建
	 */
	fun build(): KtorClient
}