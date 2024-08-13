package cn.vividcode.multiplatform.ktorfit.api.mock

import cn.vividcode.multiplatform.ktorfit.annotation.MockDsl
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/11 03:52
 *
 * 文件介绍：MockClientConfig
 */
@MockDsl
class MockRequestBuilder(
	val json: Json
) {
	
	val headers = mutableMapOf<String, Any>()
	
	val queries = mutableMapOf<String, Any>()
	
	val forms = mutableMapOf<String, Any>()
	
	val paths = mutableMapOf<String, Any>()
	
	var body: String? = null
	
	fun bearerAuth(token: String) {
		this.headers[HttpHeaders.Authorization] = "Bearer $token"
	}
	
	fun headers(block: MutableMap<String, Any>.() -> Unit) {
		this.headers += mutableMapOf<String, Any>().apply(block)
	}
	
	fun queries(block: MutableMap<String, Any>.() -> Unit) {
		this.queries += mutableMapOf<String, Any>().apply(block)
	}
	
	fun forms(block: MutableMap<String, Any>.() -> Unit) {
		this.forms += mutableMapOf<String, Any>().apply(block)
	}
	
	fun paths(block: MutableMap<String, Any>.() -> Unit) {
		this.paths += mutableMapOf<String, Any>().apply(block)
	}
	
	inline fun <reified T : Any> body(body: T) {
		this.body = json.encodeToString(body)
	}
	
	fun MutableMap<String, Any>.append(name: String, value: Any) {
		this[name] = value
	}
}