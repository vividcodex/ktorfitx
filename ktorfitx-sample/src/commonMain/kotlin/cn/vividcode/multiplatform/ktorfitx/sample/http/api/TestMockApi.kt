package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import cn.vividcode.multiplatform.ktorfitx.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.mock.MockStatus
import cn.vividcode.multiplatform.ktorfitx.sample.http.TestApiScope

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