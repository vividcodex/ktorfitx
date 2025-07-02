package cn.vividcode.multiplatform.ktorfitx.sample.http.mock

import cn.vividcode.multiplatform.ktorfitx.core.model.ApiResult
import cn.vividcode.multiplatform.ktorfitx.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.mock.MockStatus
import cn.vividcode.multiplatform.ktorfitx.mock.MockStatus.*
import cn.vividcode.multiplatform.ktorfitx.sample.http.api.TestResponse
import cn.vividcode.multiplatform.ktorfitx.sample.http.listener.TestException

data object ApiResultMockProvider : MockProvider<ApiResult<TestResponse>> {
	
	override fun provide(status: MockStatus): ApiResult<TestResponse> {
		return when (status) {
			SUCCESS -> ApiResult.success(TestResponse("测试Mock 参数一"), "测试Mock 操作成功")
			FAILURE -> ApiResult.failure(-1, "测试Mock 操作失败")
			EXCEPTION -> throw TestException()
		}
	}
}