package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import cn.vividcode.multiplatform.ktorfitx.api.exception.ExceptionListener
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import kotlinx.io.IOException
import kotlin.reflect.KFunction

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
	
	@POST("/test5")
	@ExceptionListeners(CustomExceptionListener::class)
	suspend fun test5(): ResultBody<String>
	
	@POST("/test6")
	@ExceptionListeners(Inner.CustomExceptionListener::class)
	suspend fun test6(): ResultBody<String>
	
	object CustomExceptionListener : ExceptionListener<IOException, ResultBody<String>> {
		override fun KFunction<*>.onExceptionListener(e: IOException): ResultBody<String> {
			return ResultBody.exception(e)
		}
	}
	
	class Inner {
		
		object CustomExceptionListener : ExceptionListener<IOException, ResultBody<String>> {
			override fun KFunction<*>.onExceptionListener(e: IOException): ResultBody<String> {
				return ResultBody.exception(e)
			}
		}
	}
}