package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.Api
import cn.vividcode.multiplatform.ktorfitx.annotation.Field
import cn.vividcode.multiplatform.ktorfitx.annotation.GET
import cn.vividcode.multiplatform.ktorfitx.annotation.POST
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody

@Api
interface TestMethod2Api {
	
	@GET("test")
	suspend fun test(): ResultBody<String>
	
	@GET("/test2")
	suspend fun test2(): ResultBody<String>
	
	@POST("/test3")
	suspend fun test3(
		@Field field1: String,
		@Field field2: Int
	): ResultBody<String>
	
	@POST("/test4")
	suspend fun test4(
		@Field field1: String,
		@Field("customField2") field2: Int
	): ResultBody<String>
}