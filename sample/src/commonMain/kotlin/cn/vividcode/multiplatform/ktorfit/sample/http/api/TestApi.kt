package cn.vividcode.multiplatform.ktorfit.sample.http.api

import cn.vividcode.multiplatform.ktorfit.annotation.*
import cn.vividcode.multiplatform.ktorfit.api.encrypt.EncryptType
import cn.vividcode.multiplatform.ktorfit.api.mock.MockStatus
import cn.vividcode.multiplatform.ktorfit.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfit.sample.http.TestApiScope
import cn.vividcode.multiplatform.ktorfit.sample.http.mock.TestMockProvider
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
		@Query @Encrypt(EncryptType.SHA512, 2) param2: String,
	): ResultBody<TestResponse>
	
	@POST(url = "/test02")
	@Headers("Content-Type: application/json")
	suspend fun test02(
		@Body request: TestRequest,
		@Header testHeader: String
	): ResultBody<TestResponse>
	
	@PUT(url = "/test03")
	suspend fun test03(
		@Form form1: String
	): ResultBody<TestResponse>
	
	@BearerAuth
	@DELETE(url = "/test04/{deleteId}")
	suspend fun test04(
		@Path deleteId: Int,
	): ResultBody<TestResponse>
	
	@PATCH(url = "/test05")
	suspend fun test05(): ByteArray
	
	@OPTIONS(url = "/test06")
	suspend fun test06()
	
	@HEAD(url = "/test07")
	suspend fun test07()
	
	@GET(url = "/test08")
	suspend fun test08(catch: Catch<Exception>, finally: Finally): ByteArray
	
	@BearerAuth
	@Mock(TestMockProvider::class, MockStatus.SUCCESS)
	@GET(url = "/testMock01")
	suspend fun testMock01(
		@Query param1: String,
		@Query @Encrypt param2: String,
	): ResultBody<TestResponse>
	
	@Mock(TestMockProvider::class)
	@POST(url = "/testMock02")
	suspend fun testMock02(
		@Body request: TestResponse,
	): ResultBody<TestResponse>
	
	@Mock(TestMockProvider::class)
	@PUT(url = "/testMock03")
	suspend fun testMock03(
		@Form form1: String
	): ResultBody<TestResponse>
	
	@Mock(TestMockProvider::class)
	@DELETE(url = "/testMock04/{deleteId}")
	suspend fun testMock04(
		@Path deleteId: Int,
	): ResultBody<TestResponse>
}

@Serializable
data class TestRequest(
	val param1: String,
	val param2: String
)

@Serializable
data class TestResponse(
	val param1: String
)