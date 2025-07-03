package cn.ktorfitx.multiplatform.sample.http.mock

import cn.ktorfitx.multiplatform.mock.MockProvider
import cn.ktorfitx.multiplatform.mock.MockStatus

object StringMockProvider : MockProvider<String> {
	
	override fun provide(status: MockStatus): String {
		return status.toString()
	}
}