package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.HttpMethod
import cn.ktorfitx.multiplatform.annotation.Mock
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@Api("custom")
interface TestHttpMethodApi {
	
	@CUSTOM("test1")
	suspend fun test1(): String
	
	@Mock(StringMockProvider::class)
	@CUSTOM("test2")
	suspend fun test2(): String
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
@HttpMethod("CUSTOM")
annotation class CUSTOM(
	val url: String
)