package cn.ktorfitx.multiplatform.mock

import cn.ktorfitx.multiplatform.annotation.MockDsl
import cn.ktorfitx.multiplatform.mock.config.LogConfig
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@MockDsl
class MockClient internal constructor(
	val log: LogConfig = LogConfig(),
	val json: Json = Json
) {
	
	suspend inline fun <reified R> get(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Get, mockProvider, serializer<R>(), delay, builder)
	
	suspend inline fun <reified R> post(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Post, mockProvider, serializer<R>(), delay, builder)
	
	suspend inline fun <reified R> put(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Put, mockProvider, serializer<R>(), delay, builder)
	
	suspend inline fun <reified R> delete(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Delete, mockProvider, serializer<R>(), delay, builder)
	
	suspend inline fun <reified R> patch(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Patch, mockProvider, serializer<R>(), delay, builder)
	
	suspend inline fun <reified R> head(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Head, mockProvider, serializer<R>(), delay, builder)
	
	suspend inline fun <reified R> options(
		mockProvider: MockProvider<R>,
		delay: Long,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(HttpMethod.Options, mockProvider, serializer<R>(), delay, builder)
	
	suspend fun <R> request(
		method: HttpMethod,
		mockProvider: MockProvider<R>,
		serializer: KSerializer<R>,
		delay: Long,
		builder: suspend MockRequestBuilder.() -> Unit
	): R {
		val mockRequest = MockRequestBuilder(this.json)
			.apply { builder() }
		val mockLogging = MockLogging(this.log, mockRequest, mockRequest.urlString!!)
		mockLogging.request(method)
		delay(delay)
		return mockProvider.provide().also {
			mockLogging.response(it, serializer, json, delay)
		}
	}
}