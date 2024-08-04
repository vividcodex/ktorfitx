package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.annotation.BuilderDsl
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.set

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/7 上午3:21
 *
 * 介绍：MockClientConfig
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