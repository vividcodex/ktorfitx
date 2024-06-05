package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.KtorClient

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午9:24
 *
 * 介绍：KtorClientBuilder
 */
sealed interface KtorClientBuilder : Builder<KtorClientBuilder> {
	
	/**
	 * 域名
	 */
	fun domain(
		host: String = "localhost",
		port: Int = 8080,
		safe: Boolean = false,
		prefix: String = ""
	): KtorClientBuilder {
		val domain = "${if (safe) "https://" else "http://"}$host:$port$prefix"
		return domain(domain)
	}
	
	/**
	 * 构建
	 */
	fun build(): KtorClient
}