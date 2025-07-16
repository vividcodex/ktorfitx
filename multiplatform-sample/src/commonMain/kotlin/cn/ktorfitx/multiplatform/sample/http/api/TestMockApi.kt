package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.Test2ApiScope
import cn.ktorfitx.multiplatform.sample.http.TestApiScope
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
	
	@GET(url = "/mockTest3")
	@Mock(provider = StringMockProvider::class)
	suspend fun mockTest3(): String
}