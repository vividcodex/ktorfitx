package cn.vividcode.multiplatform.ktorfit.api.mock

import cn.vividcode.multiplatform.ktorfit.api.annotation.BuilderDsl
import cn.vividcode.multiplatform.ktorfit.api.builder.mock.MockModel
import cn.vividcode.multiplatform.ktorfit.api.mock.plugin.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 * mock get
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.get(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Get, url, name, block)

/**
 * mock post
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.post(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Post, url, name, block)

/**
 * mock put
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.put(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Put, url, name, block)

/**
 * mock delete
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.delete(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Delete, url, name, block)

/**
 * mock patch
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.patch(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Patch, url, name, block)

/**
 * mock options
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.options(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Options, url, name, block)

/**
 * mock head
 */
@BuilderDsl
suspend inline fun <reified T : Any> MockClient.head(
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T = request(HttpMethod.Head, url, name, block)

@BuilderDsl
suspend inline fun <reified T : Any> MockClient.request(
	httpMethod: HttpMethod,
	url: String,
	name: String,
	noinline block: MockClientDsl.() -> Unit
): T {
	val (fullUrl, mockModel, mockLogging) = doRequest(httpMethod, url, name, T::class, block)
	
	val delay = mockModel.durationRange.random()
	delay(delay)
	
	mockLogging.loggingResponse(fullUrl, name, delay, mockModel.result)
	
	return mockModel.result
}

fun <T : Any> MockClient.doRequest(
	httpMethod: HttpMethod,
	url: String,
	name: String,
	kClass: KClass<T>,
	block: MockClientDsl.() -> Unit
): Triple<String, MockModel<T>, MockLogging> {
	val model = MockClientDslImpl().apply(block).build()
	val fullUrl = formatUrl(url, model)
	val mockLogging = getPlugin(MockLogging)
	mockLogging.loggingRequest(httpMethod, fullUrl, name, model)
	
	val mockCache = getPlugin(MockCache)
	val mockModel = mockCache.getMockModel(url, name, kClass)
	
	return Triple(fullUrl, mockModel, mockLogging)
}

/**
 * 格式化Url
 */
private fun formatUrl(url: String, model: MockClientModel): String {
	return buildString {
		var pathUrl = url
		model.paths.forEach { (name, value) ->
			pathUrl = pathUrl.replace("{$name}", value.toString())
		}
		append(pathUrl)
		model.queries.keys.forEachIndexed { index, key ->
			append(if (index == 0) '?' else '&')
			append(key)
			append('=')
			append(model.queries[key]!!.toString())
		}
	}
}