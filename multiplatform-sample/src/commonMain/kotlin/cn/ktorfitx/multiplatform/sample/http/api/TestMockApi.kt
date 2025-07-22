package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.Test2ApiScope
import cn.ktorfitx.multiplatform.sample.http.TestApiScope
import cn.ktorfitx.multiplatform.sample.http.TestRequest2
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@ApiScopes(TestApiScope::class, Test2ApiScope::class)
@Api(url = "/mock")
interface TestMockApi {
	
	@POST(url = "/{path}")
	@Mock(provider = StringMockProvider::class)
	suspend fun mockTest1(
		@Path path: String,
	): Result<String>
	
	@GET(url = "/mockTest2")
	@Mock(provider = StringMockProvider::class)
	suspend fun mockTest2(): Result<String>
	
	@POST(url = "/mockTest3")
	@Headers("Content-Type: application/json")
	@Mock(StringMockProvider::class, delay = 200L)
	suspend fun mockTest3(
		@Body(format = SerializationFormat.XML) testRequest: TestRequest2,
		@Header testHeader: String,
	): Result<String>
	
	@Mock(provider = StringMockProvider::class)
	@POST("/mockTest4")
	suspend fun mockTest4(
		@Field field1: String,
		@Field("custom") field2: Int
	): Result<String>
}