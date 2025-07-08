package cn.ktorfitx.multiplatform.sample.http.mock

import cn.ktorfitx.multiplatform.mock.MockProvider
import cn.ktorfitx.multiplatform.sample.http.api.TestResponse

data object ApiResultMockProvider : MockProvider<TestResponse> {
	
	override fun provide(): TestResponse {
		throw IllegalStateException("错误")
	}
}