package cn.ktorfitx.multiplatform.mock

import cn.ktorfitx.multiplatform.annotation.MockDsl
import cn.ktorfitx.multiplatform.mock.config.LogConfig
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

@MockDsl
class MockClient internal constructor(
	val log: LogConfig = LogConfig(),
	val format: StringFormat? = null
) {
	
	suspend inline fun <reified R> request(
		method: HttpMethod,
		mockProvider: MockProvider<R>,
		delay: Long = 0L,
		noinline builder: suspend MockRequestBuilder.() -> Unit
	): R = request(method, mockProvider, serializer<R>(), delay, builder)
	
	suspend fun <R> request(
		method: HttpMethod,
		mockProvider: MockProvider<R>,
		serializer: KSerializer<R>,
		delay: Long,
		builder: suspend MockRequestBuilder.() -> Unit
	): R {
		val mockRequest = MockRequestBuilder(this.format)
			.apply { builder() }
		val mockLogging = MockLogging(this.log, mockRequest, mockRequest.urlString!!)
		mockLogging.request(method)
		delay(delay)
		return mockProvider.provide().also {
			mockLogging.response(it, serializer, format, delay)
		}
	}
}