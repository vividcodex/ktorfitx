package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import cn.vividcode.multiplatform.ktorfitx.core.exception.ExceptionListener
import cn.vividcode.multiplatform.ktorfitx.core.model.ApiResult
import kotlinx.io.IOException
import kotlin.reflect.KFunction

@Api
interface TestMethod2Api {
	
	@GET("test")
	suspend fun test(): ApiResult<String>
	
	@GET("/test2")
	suspend fun test2(): ApiResult<String>
	
	@POST("/test3")
	suspend fun test3(
		@Field field1: String,
		@Field field2: Int
	): ApiResult<String>
	
	@POST("/test4")
	suspend fun test4(
		@Field field1: String,
		@Field("customField2") field2: Int
	): ApiResult<String>
	
	@POST("/test5")
	@ExceptionListeners(CustomExceptionListener::class)
	suspend fun test5(): ApiResult<String>
	
	@POST("/test6")
	@ExceptionListeners(Inner.CustomExceptionListener::class)
	suspend fun test6(): ApiResult<String>
	
	object CustomExceptionListener : ExceptionListener<IOException, ApiResult<String>> {
		override fun KFunction<*>.onExceptionListener(e: IOException): ApiResult<String> {
			return ApiResult.exception(e)
		}
	}
	
	class Inner {
		
		object CustomExceptionListener : ExceptionListener<IOException, ApiResult<String>> {
			override fun KFunction<*>.onExceptionListener(e: IOException): ApiResult<String> {
				return ApiResult.exception(e)
			}
		}
	}
}