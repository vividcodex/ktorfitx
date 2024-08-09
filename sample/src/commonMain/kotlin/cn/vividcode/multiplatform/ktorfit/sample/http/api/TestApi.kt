package cn.vividcode.multiplatform.ktorfit.sample.http.api

import cn.vividcode.multiplatform.ktorfit.annotation.*
import cn.vividcode.multiplatform.ktorfit.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfit.sample.http.TestApiScope
import kotlinx.serialization.Serializable

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/4 23:09
 *
 * 文件介绍：TestApi
 */
@Api(url = "/test", apiScope = TestApiScope::class)
interface TestApi {
	
	@GET(url = "/test01")
	suspend fun test01(
		@Query param1: String,
		@Query param2: String,
	): ResultBody<TestResponse>
	
	@POST(url = "/test02")
	@Headers("Content-Type: application/json")
	suspend fun test02(
		@Body request: Test01Request,
		@Header testHeader: String
	): ResultBody<TestResponse>
	
	@PUT(url = "/test03")
	suspend fun test03(
		@Form form1: String
	): ResultBody<TestResponse>
	
	@DELETE(url = "/test04/{deleteId}", auth = true)
	suspend fun test04(
		@Path deleteId: Int,
	): ResultBody<TestResponse>
	
	@PATCH(url = "/test05")
	suspend fun test05(): ByteArray
	
	@OPTIONS(url = "/test06")
	suspend fun test06()
	
	@HEAD(url = "/test07")
	suspend fun test07()
	
	@Mock(name = "mock01")
	@GET(url = "/testMock01")
	suspend fun testMock01(
		@Query param1: String,
		@Query param2: String,
	): ResultBody<TestResponse>
	
	@Mock(name = "mock02")
	@POST(url = "/testMock02")
	suspend fun testMock02(
		@Body request: Test01Request,
	): ResultBody<TestResponse>
	
	@Mock(name = "mock03")
	@PUT(url = "/testMock03")
	suspend fun testMock03(
		@Form form1: String
	): ResultBody<TestResponse>
	
	@Mock(name = "mock04")
	@DELETE(url = "/testMock04/{deleteId}")
	suspend fun testMock04(
		@Path deleteId: Int,
	): ResultBody<TestResponse>
}

@Serializable
data class Test01Request(
	val param1: String,
	val param2: String
)

@Serializable
data class TestResponse(
	val param1: String
)