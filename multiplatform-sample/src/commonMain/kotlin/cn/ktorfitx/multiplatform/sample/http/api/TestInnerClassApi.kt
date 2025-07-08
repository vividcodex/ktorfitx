package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.GET
import kotlinx.serialization.Serializable

@Api(url = "test3")
interface TestInnerClassApi {
	
	@GET("v1/test1")
	suspend fun getTest1(): Result<Test1>
	
	@GET("v1/test2")
	suspend fun getTest2(): Result<Test2>
	
	@GET("v1/test3")
	suspend fun getTest3(): Result<Test3Class.Test3>
	
	@GET("v1/test4")
	suspend fun getTest4(): Result<Test4Class.Test4>
	
	@Serializable
	data class Test1(val data: String)
	
	class Test3Class {
		
		@Serializable
		data class Test3(val data: String)
	}
}

@Serializable
data class Test2(val data: String)

class Test4Class {
	
	@Serializable
	data class Test4(val data: String)
}