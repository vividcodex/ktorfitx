package cn.vividcode.multiplatform.ktor.client.api.builder

import io.ktor.client.plugins.logging.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/13 上午2:19
 *
 * 介绍：Builder
 */
sealed interface Builder<R> {
	
	/**
	 * 域名
	 */
	fun domain(domain: String): R
	
	/**
	 * 获取Token
	 */
	fun getToken(getToken: () -> String): R
	
	/**
	 * 连接超时时长 (ms)
	 */
	fun connectTimeout(connectTimeout: Long): R
	
	/**
	 * Socket 超时时长 (ms)
	 */
	fun socketTimeout(socketTimeout: Long): R
	
	/**
	 * 线程池线程空闲时间（ms）
	 */
	fun keepAliveTime(keepAliveTime: Long): R
	
	/**
	 * 日志级别
	 */
	fun logLevel(logLevel: LogLevel): R
	
	/**
	 * 日志处理
	 */
	fun handleLog(handleLog: (message: String) -> Unit): R
}