package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@Api(url = "dynamicUrl")
interface TestDynamicUrlApi {
	
	@GET
	suspend fun test1(
		@DynamicUrl url: String
	): String
	
	@GET
	suspend fun test2(
		@DynamicUrl url: String,
		@Path name: String,
		@Path("customId") id: String
	): String
	
	@CUSTOM
	suspend fun test3(
		@DynamicUrl url: String,
	): String
	
	@Mock(provider = StringMockProvider::class, delay = 200L)
	@GET
	suspend fun test4(
		@DynamicUrl url: String,
		@Path name: String,
		@Path("customId") id: String
	): String
}