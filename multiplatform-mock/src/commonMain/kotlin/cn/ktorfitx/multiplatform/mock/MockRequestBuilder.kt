package cn.ktorfitx.multiplatform.mock

import cn.ktorfitx.multiplatform.annotation.MockDsl
import io.ktor.http.*
import kotlinx.serialization.json.Json

@MockDsl
class MockRequestBuilder(
	val json: Json
) {
	
	val headers = mutableMapOf<String, Any>()
	
	val queries = mutableMapOf<String, Any>()
	
	val parts = mutableMapOf<String, Any>()
	
	val fields = mutableMapOf<String, Any>()
	
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
	
	fun parts(block: MutableMap<String, Any>.() -> Unit) {
		this.parts += mutableMapOf<String, Any>().apply(block)
	}
	
	fun fields(block: MutableMap<String, Any>.() -> Unit) {
		this.fields += mutableMapOf<String, Any>().apply(block)
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