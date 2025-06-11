package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.Api
import cn.vividcode.multiplatform.ktorfitx.annotation.GET
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody

@Api
interface TestMethod2Api {
	
	@GET("test")
	suspend fun test(): ResultBody<String>
	
	@GET("/test2")
	suspend fun test2(): ResultBody<String>
}