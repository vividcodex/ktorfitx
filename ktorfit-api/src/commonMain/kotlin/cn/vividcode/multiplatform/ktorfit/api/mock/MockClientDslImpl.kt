package cn.vividcode.multiplatform.ktorfit.api.mock

import cn.vividcode.multiplatform.ktorfit.api.annotation.BuilderDsl
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.set

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/7 3:21
 *
 * 文件介绍：MockClientConfig
 */
@BuilderDsl
sealed interface MockClientDsl {
	
	val headers: MutableMap<String, Any>
	
	val queries: MutableMap<String, Any>
	
	val forms: MutableMap<String, Any>
	
	val paths: MutableMap<String, Any>
	
	var body: String?
}

internal class MockClientDslImpl : MockClientDsl {
	
	override val headers = mutableMapOf<String, Any>()
	
	override val queries = mutableMapOf<String, Any>()
	
	override val forms = mutableMapOf<String, Any>()
	
	override val paths = mutableMapOf<String, Any>()
	
	override var body: String? = null
	
	internal fun build(): MockClientModel {
		return MockClientModel(headers, queries, forms, paths, body)
	}
}

/**
 * bearerAuth
 */
fun MockClientDsl.bearerAuth(token: String) {
	this.headers[HttpHeaders.Authorization] = "Bearer $token"
}

/**
 * headers
 */
fun MockClientDsl.headers(block: MockClientMapDsl.() -> Unit) {
	this.headers += MockClientMapDslImpl().apply(block).valueMap
}

/**
 * queries
 */
fun MockClientDsl.queries(block: MockClientMapDsl.() -> Unit) {
	this.queries += MockClientMapDslImpl().apply(block).valueMap
}

/**
 * forms
 */
fun MockClientDsl.forms(block: MockClientMapDsl.() -> Unit) {
	this.forms += MockClientMapDslImpl().apply(block).valueMap
}

/**
 * paths
 */
fun MockClientDsl.paths(block: MockClientMapDsl.() -> Unit) {
	this.paths += MockClientMapDslImpl().apply(block).valueMap
}

/**
 * body
 */
inline fun <reified T : Any> MockClientDsl.body(body: T) {
	this.body = Json.encodeToString(body)
}