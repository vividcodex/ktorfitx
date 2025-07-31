package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider
import kotlinx.serialization.Serializable

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
	
	@POST("test1")
	@Mock(StringMockProvider::class)
	suspend fun test3(
		@Attribute name: String,
		@Attribute("custom") age: Int
	): String
	
	@BearerAuth
	@POST(url = "user/detail/update")
	suspend fun updateUserDetail(
		@Body data: UserDetail
	): Boolean
}

@Serializable
data class UserDetail(
	val a: String,
)