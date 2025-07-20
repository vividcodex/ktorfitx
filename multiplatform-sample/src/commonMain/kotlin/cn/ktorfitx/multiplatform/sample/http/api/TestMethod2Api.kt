package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@Api
interface TestMethod2Api {
	
	@GET("test1")
	suspend fun test1(): Result<String>
	
	@GET("/test2")
	suspend fun test2(): Result<String>
	
	@Timeout(
		requestTimeoutMillis = 10_000L,
		connectTimeoutMillis = 2_000L,
	)
	@POST("/test3")
	suspend fun test3(
		@Field field1: String,
		@Field field2: Int
	): Result<String>
	
	@POST("/test4")
	suspend fun test4(
		@Field field1: String,
		@Field("customField2") field2: Int?
	): Result<String>
	
	@POST("/test5")
	suspend fun test5(): Result<String>
	
	@Timeout(
		requestTimeoutMillis = 10_000L,
		connectTimeoutMillis = 2_000L,
	)
	@Mock(StringMockProvider::class)
	@POST("/mockTest6")
	suspend fun mockTest6(
		@Field field1: String,
		@Field field2: Int
	): Result<String>
}