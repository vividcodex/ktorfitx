package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockStatus
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfitx.sample.http.TestApiScope
import cn.vividcode.multiplatform.ktorfitx.sample.http.TestRequest2
import cn.vividcode.multiplatform.ktorfitx.sample.http.listener.TestResultBodyExceptionListener
import cn.vividcode.multiplatform.ktorfitx.sample.http.listener.TestUnitExceptionListener
import cn.vividcode.multiplatform.ktorfitx.sample.http.mock.ResultBodyMockProvider
import cn.vividcode.multiplatform.ktorfitx.sample.http.mock.StringMockProvider
import kotlinx.serialization.Serializable

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/4 23:09
 *
 * 文件介绍：TestApi
 */
@Api(url = "user", apiScope = TestApiScope::class)
interface TestApi {
	
	@GET(url = "test01")
	@ExceptionListeners(TestResultBodyExceptionListener::class)
	suspend fun test01(): String
	
	@POST(url = "/test02")
	@Headers("Content-Type: application/json")
	@ExceptionListeners(TestUnitExceptionListener::class)
	suspend fun test02(
		@Body testRequest: TestRequest2,
		@Header testHeader: String,
	): ResultBody<TestResponse>
	
	@BearerAuth
	@PUT(url = "/test03")
	suspend fun test03(
		@Form form1: String,
	): ResultBody<TestResponse>
	
	@BearerAuth
	@DELETE(url = "/test04/{deleteId}")
	suspend fun test04(
		@Path deleteId: Int,
	): ResultBody<TestResponse>
	
	@PATCH(url = "/test05")
	suspend fun test05(
		@Form form1: String,
	): ByteArray
	
	@BearerAuth
	@OPTIONS(url = "/{name}/test06")
	suspend fun test06(
		@Path name: String,
	): ByteArray
	
	@HEAD(url = "/test07")
	suspend fun test07()
	
	@ExceptionListeners(TestUnitExceptionListener::class)
	@GET(url = "/test08")
	suspend fun test08(): ByteArray?
	
	@GET(url = "/test09")
	suspend fun test09(): String
	
	@POST(url = "/test10")
	suspend fun test10(): String?
	
	@BearerAuth
	@Mock(ResultBodyMockProvider::class, MockStatus.SUCCESS, delayRange = [1000, 2000])
	@GET(url = "/testMock01")
	suspend fun testMock01(
		@Query param1: String,
		@Query param2: String,
	): ResultBody<TestResponse>
	
	@Mock(ResultBodyMockProvider::class, MockStatus.EXCEPTION)
	@ExceptionListeners(TestUnitExceptionListener::class)
	@POST(url = "/testMock02")
	suspend fun testMock02(
		@Body request: TestRequest,
	): ResultBody<TestResponse>
	
	@Mock(ResultBodyMockProvider::class)
	@PUT(url = "/testMock03")
	suspend fun testMock03(
		@Form form1: String,
	): ResultBody<TestResponse>
	
	@Mock(ResultBodyMockProvider::class)
	@DELETE(url = "/testMock04/{deleteId}")
	suspend fun testMock04(
		@Path deleteId: Int,
	): ResultBody<TestResponse>
	
	@Mock(StringMockProvider::class)
	@GET(url = "/testMock05")
	suspend fun testMock05(): String
	
	@Mock(StringMockProvider::class)
	@GET(url = "/testMock06")
	suspend fun testMock06(): String?
	
	@GET(url = "http://v.juhe.cn/toutiao/index")
	suspend fun headlineNews(
		@Query("key") key: String,
		@Query("type") type: String,
		@Query("page") page: String,
		@Query("page_size") pageSize: String,
		@Query("is_filter") isFilter: Int,
	): String?
	
	@GET(url = "/test")
	suspend fun test(): ResultBody<Test>
}

@Serializable
data class TestRequest(
	val param1: String,
	val param2: String,
)

@Serializable
data class TestResponse(
	val param1: String,
)