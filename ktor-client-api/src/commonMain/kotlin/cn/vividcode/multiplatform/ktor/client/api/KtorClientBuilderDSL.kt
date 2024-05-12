package cn.vividcode.multiplatform.ktor.client.api

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/13 上午2:19
 *
 * 介绍：KtorClientBuilderDSL
 */
interface KtorClientBuilderDSL {
	
	/**
	 * 域名
	 */
	fun domain(domain: String)
	
	/**
	 * 连接超时时长 (ms)
	 */
	fun connectTimeout(connectTimeout: Long)
	
	/**
	 * Socket 超时时长 (ms)
	 */
	fun socketTimeout(socketTimeout: Long)
	
	/**
	 * 日志处理
	 */
	fun handleLog(handleLog: (message: String) -> Unit)
	
	/**
	 * 获取Token
	 */
	fun getToken(getToken: () -> String)
}