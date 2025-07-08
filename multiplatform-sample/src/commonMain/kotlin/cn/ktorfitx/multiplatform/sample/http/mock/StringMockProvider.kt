package cn.ktorfitx.multiplatform.sample.http.mock

import cn.ktorfitx.multiplatform.mock.MockProvider

object StringMockProvider : MockProvider<String> {
	
	override fun provide(): String {
		return "StringMockProvider"
	}
}