package cn.vividcode.multiplatform.ktor.client.api.config

import cn.vividcode.multiplatform.ktor.client.api.mock.MockModel
import kotlin.reflect.KClass

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午9:04
 *
 * 介绍：KtorConfig
 */
class KtorConfig internal constructor(
	var baseUrl: String = "http://localhost",
	var jwt: (() -> String)? = null,
	var mocksMap: Map<KClass<*>, Map<String, MockModel<*>>> = emptyMap()
) {
	
	/**
	 * 检查
	 */
	fun check() {
		check(this.baseUrl.isNotEmpty()) { "domain 还没有配置" }
		checkNotNull(this.jwt) { "getToken 还没有配置" }
	}
}