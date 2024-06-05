package cn.vividcode.multiplatform.ktor.client.api.builder

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午9:25
 *
 * 介绍：KtorClientBuilderDSL
 */
sealed interface KtorClientBuilderDSL : Builder<Unit> {
	
	/**
	 * 域名
	 */
	fun domain(builder: DomainBuilderDSL.() -> Unit) {
		DomainBuilderDSL().apply {
			builder()
			val domain = "${if (safe) "https://" else "http://"}$host:$port$prefix"
			domain(domain)
		}
	}
	
	data class DomainBuilderDSL internal constructor(
		var host: String = "localhost",
		var port: Int = 8080,
		var safe: Boolean = false,
		var prefix: String = ""
	)
}