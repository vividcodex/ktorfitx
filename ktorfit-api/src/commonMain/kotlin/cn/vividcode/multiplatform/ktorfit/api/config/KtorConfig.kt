package cn.vividcode.multiplatform.ktorfit.api.config

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/14 21:04
 *
 * 文件介绍：KtorConfig
 */
class KtorConfig internal constructor(
	var baseUrl: String = "",
	var token: (() -> String)? = null
) {
	
	/**
	 * 检查
	 */
	fun check() {
		check(this.baseUrl.isNotEmpty()) { "baseUrl 没有配置" }
		checkNotNull(this.token) { "token 没有配置" }
	}
}