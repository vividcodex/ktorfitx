package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.mock.MockProvider
import cn.ktorfitx.multiplatform.mock.MockStatus
import cn.ktorfitx.multiplatform.sample.http.TestApiScope

@Api(url = "/test2", apiScope = TestApiScope::class)
interface TestMockApi {
	
	@POST(url = "/{path}")
	suspend fun test(
		@Path path: String,
	): String
	
	@GET(url = "/mockTest")
	@Mock(provider = TestMockProvider::class)
	suspend fun mockTest(): String
}

object TestMockProvider : MockProvider<String> {
	override fun provide(status: MockStatus): String {
		return ""
	}
}