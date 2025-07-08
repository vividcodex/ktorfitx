package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.TestApiScope
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@Api(url = "/test2", apiScope = TestApiScope::class)
interface TestMockApi {
	
	@POST(url = "/{path}")
	suspend fun test(
		@Path path: String,
	): Result<String>
	
	@GET(url = "/mockTest")
	@Mock(provider = StringMockProvider::class)
	suspend fun mockTest(): Result<String>
}