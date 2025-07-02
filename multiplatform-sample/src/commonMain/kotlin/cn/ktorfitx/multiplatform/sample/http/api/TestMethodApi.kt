package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.core.model.ApiResult
import cn.ktorfitx.multiplatform.mock.MockStatus
import cn.ktorfitx.multiplatform.sample.http.TestApiScope
import cn.ktorfitx.multiplatform.sample.http.TestRequest2
import cn.ktorfitx.multiplatform.sample.http.listener.TestStringExceptionListener
import cn.ktorfitx.multiplatform.sample.http.listener.TestUnitExceptionListener
import cn.ktorfitx.multiplatform.sample.http.mock.ApiResultMockProvider
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider
import kotlinx.serialization.Serializable

@Api(url = "test1", apiScope = TestApiScope::class)
interface TestMethodApi {
	
	@GET(url = "test01")
	@ExceptionListeners(TestStringExceptionListener::class)
	suspend fun test01(): String
	
	@POST(url = "/test02")
	@Headers("Content-Type: application/json")
	@ExceptionListeners(TestUnitExceptionListener::class)
	suspend fun test02(
		@Body testRequest: TestRequest2,
		@Header testHeader: String,
	): ApiResult<TestResponse>
	
	@BearerAuth
	@PUT(url = "/test03")
	suspend fun test03(
		@Part part1: String,
	): ApiResult<TestResponse>
	
	@BearerAuth
	@DELETE(url = "/test04/{deleteId}")
	suspend fun test04(
		@Path deleteId: Int,
	): ApiResult<TestResponse>
	
	@PATCH(url = "/test05")
	suspend fun test05(
		@Part part1: String,
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
	@Mock(ApiResultMockProvider::class, MockStatus.SUCCESS, delayRange = [1000, 2000])
	@GET(url = "/testMock01")
	suspend fun testMock01(
		@Query param1: String,
		@Query param2: String,
	): ApiResult<TestResponse>
	
	@Mock(ApiResultMockProvider::class, MockStatus.EXCEPTION)
	@ExceptionListeners(TestUnitExceptionListener::class)
	@POST(url = "/testMock02")
	suspend fun testMock02(
		@Body request: TestRequest,
	): ApiResult<TestResponse>
	
	@Mock(ApiResultMockProvider::class)
	@PUT(url = "/testMock03")
	suspend fun testMock03(
		@Part part1: String,
	): ApiResult<TestResponse>
	
	@Mock(ApiResultMockProvider::class)
	@DELETE(url = "/testMock04/{deleteId}")
	suspend fun testMock04(
		@Path deleteId: Int,
	): ApiResult<TestResponse>
	
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
	suspend fun test(): ApiResult<Test>
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

@Serializable
data class Test(
	val test: String
)