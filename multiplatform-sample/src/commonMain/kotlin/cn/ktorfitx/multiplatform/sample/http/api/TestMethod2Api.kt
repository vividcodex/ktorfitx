package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*

@Api
interface TestMethod2Api {
	
	@GET("test")
	suspend fun test(): Result<String>
	
	@GET("/test2")
	suspend fun test2(): Result<String>
	
	@POST("/test3")
	suspend fun test3(
		@Field field1: String,
		@Field field2: Int
	): Result<String>
	
	@POST("/test4")
	suspend fun test4(
		@Field field1: String,
		@Field("customField2") field2: Int
	): Result<String>
	
	@POST("/test5")
	suspend fun test5(): Result<String>
	
	@Api("test")
	interface TestApi {
		
		@BearerAuth
		@GET("test01")
		suspend fun test01(): Result<String>
	}
}