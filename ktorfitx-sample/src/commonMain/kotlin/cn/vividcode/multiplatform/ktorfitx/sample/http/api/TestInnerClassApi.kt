package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.Api
import cn.vividcode.multiplatform.ktorfitx.annotation.GET
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import kotlinx.serialization.Serializable

@Api(url = "test3")
interface TestInnerClassApi {
	
	@GET("v1/test1")
	suspend fun getTest1(): ResultBody<Test1>
	
	@GET("v1/test2")
	suspend fun getTest2(): ResultBody<Test2>
	
	@GET("v1/test3")
	suspend fun getTest3(): ResultBody<Test3Class.Test3>
	
	@GET("v1/test4")
	suspend fun getTest4(): ResultBody<Test4Class.Test4>
	
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