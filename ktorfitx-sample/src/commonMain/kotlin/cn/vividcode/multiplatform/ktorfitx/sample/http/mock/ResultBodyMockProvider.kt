package cn.vividcode.multiplatform.ktorfitx.sample.http.mock

import cn.vividcode.multiplatform.ktorfitx.api.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockStatus
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockStatus.*
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfitx.sample.http.api.TestResponse
import cn.vividcode.multiplatform.ktorfitx.sample.http.listener.TestException

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 04:04
 *
 * 文件介绍：TestMockProvider
 */
object ResultBodyMockProvider : MockProvider<ResultBody<TestResponse>> {
	
	override fun provide(status: MockStatus): ResultBody<TestResponse> {
		return when (status) {
			SUCCESS -> ResultBody.success(TestResponse("测试Mock 参数一"), "测试Mock 操作成功")
			FAILURE -> ResultBody.failure(-1, "测试Mock 操作失败")
			EXCEPTION -> throw TestException()
		}
	}
}