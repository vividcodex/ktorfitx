package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import kotlinx.serialization.Serializable

@Api(url = "test3")
interface TestMethod3Api {
	
	@GET("test1")
	suspend fun test1(): String
	
	@POST("test2")
	suspend fun test2(): ApiResult<Unit>
	
	@PUT("test3")
	suspend fun test3(): List<String>
	
	@DELETE("test4")
	suspend fun test4()
}

@Serializable
data class ApiResult<out T : Any>(
	val code: Int,
	val msg: String,
	val data: T?
)