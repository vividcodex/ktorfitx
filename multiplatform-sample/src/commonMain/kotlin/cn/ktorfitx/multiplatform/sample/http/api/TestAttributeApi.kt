package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.Attribute
import cn.ktorfitx.multiplatform.annotation.Mock
import cn.ktorfitx.multiplatform.annotation.POST
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@Api("attribute")
interface TestAttributeApi {
	
	@POST("test1")
	suspend fun test1(
		@Attribute name: String,
		@Attribute("custom") age: Int
	): String
	
	@POST("test1")
	@Mock(StringMockProvider::class)
	suspend fun test2(
		@Attribute name: String,
		@Attribute("custom") age: Int
	): String
}