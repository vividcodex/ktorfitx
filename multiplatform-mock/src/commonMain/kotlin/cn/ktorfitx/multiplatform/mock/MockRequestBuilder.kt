package cn.ktorfitx.multiplatform.mock

import cn.ktorfitx.multiplatform.annotation.MockDsl
import io.ktor.http.*
import kotlinx.serialization.json.Json

@MockDsl
class MockRequestBuilder(
	val json: Json
) {
	
	var urlString: String? = null
		private set
	
	private val _headers = mutableMapOf<String, Any>()
	val headers: Map<String, Any> = _headers
	
	private val _queries = mutableMapOf<String, Any>()
	val queries: Map<String, Any> = _queries
	
	private val _parts = mutableMapOf<String, Any>()
	val parts: Map<String, Any> = _parts
	
	private val _fields = mutableMapOf<String, Any>()
	val fields: Map<String, Any> = _fields
	
	private val _paths = mutableMapOf<String, Any>()
	val paths: Map<String, Any> = _paths
	
	var body: String? = null
	
	fun url(urlString: String) {
		this.urlString = urlString
	}
	
	fun bearerAuth(token: String) {
		this._headers[HttpHeaders.Authorization] = "Bearer $token"
	}
	
	fun headers(block: MutableMap<String, Any>.() -> Unit) {
		this._headers += mutableMapOf<String, Any>().apply(block)
	}
	
	fun queries(block: MutableMap<String, Any>.() -> Unit) {
		this._queries += mutableMapOf<String, Any>().apply(block)
	}
	
	fun parts(block: MutableMap<String, Any>.() -> Unit) {
		this._parts += mutableMapOf<String, Any>().apply(block)
	}
	
	fun fields(block: MutableMap<String, Any>.() -> Unit) {
		this._fields += mutableMapOf<String, Any>().apply(block)
	}
	
	fun paths(block: MutableMap<String, Any>.() -> Unit) {
		this._paths += mutableMapOf<String, Any>().apply(block)
	}
	
	inline fun <reified T : Any> body(body: T) {
		this.body = json.encodeToString(body)
	}
	
	fun MutableMap<String, Any>.append(name: String, value: Any) {
		this[name] = value
	}
}