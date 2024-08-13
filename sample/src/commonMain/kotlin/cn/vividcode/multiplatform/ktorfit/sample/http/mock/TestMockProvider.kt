package cn.vividcode.multiplatform.ktorfit.sample.http.mock

import cn.vividcode.multiplatform.ktorfit.api.mock.MockProvider
import cn.vividcode.multiplatform.ktorfit.api.mock.MockStatus
import cn.vividcode.multiplatform.ktorfit.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfit.sample.http.api.TestResponse

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 04:04
 *
 * 文件介绍：TestMockProvider
 */
object TestMockProvider : MockProvider<ResultBody<TestResponse>> {
	
	override fun provide(status: MockStatus): ResultBody<TestResponse> {
		return ResultBody.success(TestResponse("测试"), "操作成功")
	}
}