package cn.vividcode.multiplatform.ktor.client.api.config

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午9:04
 *
 * 介绍：KtorConfig
 */
data class KtorConfig internal constructor(
	var domain: String = "http://localhost:8080",
	var getToken: (() -> String)? = null
) {
	
	/**
	 * 检查
	 */
	fun check() {
		check(this.domain.isNotEmpty()) { "domain 还没有配置" }
		checkNotNull(this.getToken) { "getToken 还没有配置" }
	}
}