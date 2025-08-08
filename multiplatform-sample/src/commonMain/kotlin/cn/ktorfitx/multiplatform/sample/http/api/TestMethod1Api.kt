package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.TestApiScope
import cn.ktorfitx.multiplatform.sample.http.TestRequest2
import cn.ktorfitx.multiplatform.sample.http.TestResponse
import cn.ktorfitx.multiplatform.sample.http.mock.ApiResultMockProvider
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider
import kotlinx.serialization.Serializable

@ApiScope(TestApiScope::class)
@Api(url = "test1")
interface TestMethod1Api {
	
	@GET(url = "test01")
	suspend fun test01(): Result<String>
	
	@POST(url = "/test02")
	@Headers("Content-Type: application/json")
	suspend fun test02(
		@Body testRequest: TestRequest2,
		@Header testHeader: String,
	): Result<TestResponse>
	
	@BearerAuth
	@PUT(url = "/test03")
	suspend fun test03(
		@Part part1: String,
	): Result<TestResponse>
	
	@BearerAuth
	@DELETE(url = "/test04/{deleteId}")
	suspend fun test04(
		@Path deleteId: Int,
		@Body(format = SerializationFormat.XML) testRequest: TestRequest2,
	): Result<TestResponse>
	
	@PATCH(url = "/test05")
	suspend fun test05(
		@Part part1: String,
	): Result<ByteArray>
	
	@BearerAuth
	@OPTIONS(url = "/{name}/test06")
	suspend fun test06(
		@Path name: String,
	): Result<ByteArray>
	
	@HEAD(url = "/test07")
	suspend fun test07(): Result<Unit>
	
	@GET(url = "/test08")
	suspend fun test08(): Result<ByteArray>
	
	@GET(url = "/test09")
	suspend fun test09(): Result<String>
	
	@POST(url = "/test10")
	suspend fun test10(): Result<String?>

	@POST(url = "/test11")
	suspend fun test11(@Queries map: Map<String, *>): String
	
	@BearerAuth
	@Mock(ApiResultMockProvider::class, delay = 200L)
	@GET(url = "/testMock01")
	suspend fun testMock01(
		@Query param1: String,
		@Query param2: String,
	): Result<TestResponse>
	
	@Mock(ApiResultMockProvider::class)
	@POST(url = "/testMock02")
	suspend fun testMock02(
		@Body request: TestRequest,
	): Result<TestResponse>
	
	@Mock(ApiResultMockProvider::class)
	@PUT(url = "/testMock03")
	suspend fun testMock03(
		@Part part1: String,
	): Result<TestResponse>
	
	@Mock(ApiResultMockProvider::class)
	@DELETE(url = "/testMock04/{deleteId}")
	suspend fun testMock04(
		@Path deleteId: Int,
	): Result<TestResponse>
	
	@Mock(StringMockProvider::class)
	@GET(url = "/testMock05")
	suspend fun testMock05(): Result<String>
	
	@Mock(StringMockProvider::class)
	@GET(url = "/testMock06")
	suspend fun testMock06(): Result<String>
	
	@GET(url = "http://v.juhe.cn/toutiao/index")
	suspend fun headlineNews(
		@Query("key") key: String,
		@Query("type") type: String,
		@Query("page") page: String,
		@Query("page_size") pageSize: String,
		@Query("is_filter") isFilter: Int,
	): Result<String>
	
	@GET(url = "/test")
	suspend fun test(): Result<Test>
}

@Serializable
data class TestRequest(
	val param1: String,
	val param2: String,
)

@Serializable
data class Test(
	val test: String
)