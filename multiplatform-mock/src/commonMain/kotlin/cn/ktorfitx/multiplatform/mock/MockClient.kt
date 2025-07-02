package cn.ktorfitx.multiplatform.mock

import cn.ktorfitx.multiplatform.annotation.MockDsl
import cn.ktorfitx.multiplatform.mock.config.LogConfig
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

@MockDsl
class MockClient internal constructor(
	val log: LogConfig = LogConfig(),
	val json: Json = Json
) {
	
	suspend inline fun <reified Mock : Any> get(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Get, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> post(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Post, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> put(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Put, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> delete(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Delete, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> options(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Options, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> head(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Head, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> patch(
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)? = null
	): Mock = request(HttpMethod.Patch, urlString, mockProvider, status, delayRange, builder)
	
	suspend inline fun <reified Mock : Any> request(
		method: HttpMethod,
		urlString: String,
		mockProvider: MockProvider<Mock>,
		status: MockStatus,
		delayRange: LongRange,
		noinline builder: (MockRequestBuilder.() -> Unit)?
	): Mock {
		val mockRequest = MockRequestBuilder(this.json).let { if (builder != null) it.apply(builder) else it }
		val mockLogging = MockLogging(this.log, mockRequest, urlString)
		mockLogging.request(method)
		val delay = delayRange.random()
		delay(delay)
		val mock = mockProvider.provide(status)
		mockLogging.response(mock, json, delay)
		return mock
	}
}