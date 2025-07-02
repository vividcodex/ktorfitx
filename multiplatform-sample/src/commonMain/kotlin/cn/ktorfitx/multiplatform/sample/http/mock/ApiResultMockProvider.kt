package cn.ktorfitx.multiplatform.sample.http.mock

import cn.ktorfitx.multiplatform.core.model.ApiResult
import cn.ktorfitx.multiplatform.mock.MockProvider
import cn.ktorfitx.multiplatform.mock.MockStatus
import cn.ktorfitx.multiplatform.mock.MockStatus.*
import cn.ktorfitx.multiplatform.sample.http.api.TestResponse
import cn.ktorfitx.multiplatform.sample.http.listener.TestException

data object ApiResultMockProvider : MockProvider<ApiResult<TestResponse>> {
	
	override fun provide(status: MockStatus): ApiResult<TestResponse> {
		return when (status) {
			SUCCESS -> ApiResult.success(TestResponse("测试Mock 参数一"), "测试Mock 操作成功")
			FAILURE -> ApiResult.failure(-1, "测试Mock 操作失败")
			EXCEPTION -> throw TestException()
		}
	}
}