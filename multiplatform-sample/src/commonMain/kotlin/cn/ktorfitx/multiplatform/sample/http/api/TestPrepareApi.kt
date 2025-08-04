package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import io.ktor.client.statement.*

@Api(url = "prepare")
interface TestPrepareApi {
	
	@Prepare
	@GET(url = "test")
	suspend fun test(): HttpStatement
	
	@Prepare
	@BearerAuth
	@POST(url = "test2")
	suspend fun test2(): HttpStatement
	
	@Prepare
	@CUSTOM(url = "test3")
	suspend fun test3(): HttpStatement
}