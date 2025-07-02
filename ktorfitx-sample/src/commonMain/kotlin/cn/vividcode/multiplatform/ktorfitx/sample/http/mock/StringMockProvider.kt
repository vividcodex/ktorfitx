package cn.vividcode.multiplatform.ktorfitx.sample.http.mock

import cn.vividcode.multiplatform.ktorfitx.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.mock.MockStatus

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/9/9 18:08
 *
 * 文件介绍：StringMockProvider
 */
object StringMockProvider : MockProvider<String> {
	
	override fun provide(status: MockStatus): String {
		return status.toString()
	}
}