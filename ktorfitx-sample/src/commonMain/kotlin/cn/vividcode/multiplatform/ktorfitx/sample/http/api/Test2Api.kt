package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockStatus
import cn.vividcode.multiplatform.ktorfitx.sample.http.TestApiScope

@Api(url = "/test2", apiScope = TestApiScope::class)
interface Test2Api {
	
	@POST(url = "/{path}")
	abstract suspend fun test(
		@Path path: String,
	): String
	
	@GET(url = "/mockTest")
	@Mock(provider = TestMockProvider::class)
	abstract suspend fun mockTest()
}

object TestMockProvider : MockProvider<String> {
	override fun provide(status: MockStatus): String {
		return ""
	}
}