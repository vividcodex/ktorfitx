package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.*
import cn.ktorfitx.multiplatform.sample.http.mock.StringMockProvider

@Api("testDynamicValue")
interface TestDynamicValueApi {
	
	@POST(url = "queries01")
	suspend fun queries01(
		@Queries queries: Map<String, *>
	): String
	
	@POST(url = "queries02")
	suspend fun queries02(
		@Queries queries: Map<String, *>,
		@Query id: Int
	): String
	
	@POST(url = "queries03")
	suspend fun queries03(
		@Queries queries1: Map<String, *>,
		@Queries queries2: Map<String, *>,
		@Query id: Int,
		@Query name: String
	): String
	
	@POST(url = "fields01")
	suspend fun fields01(
		@Fields fields: Map<String, String>
	): Result<List<String>>
	
	@POST(url = "fields02")
	suspend fun fields02(
		@Fields fields: Map<String, Int?>,
		@Field field: String
	): Result<List<String>>
	
	@POST(url = "fields03")
	suspend fun fields03(
		@Fields fields1: Map<String, Int>,
		@Fields fields2: Map<String, String?>,
		@Field field1: String,
		@Field field2: Int?
	): Result<List<String>>
	
	@Mock(provider = StringMockProvider::class)
	@POST(url = "/testMock01")
	suspend fun testMock01(
		@Queries map: Map<String, *>
	): String

//	@Mock(provider = StringMockProvider::class)
//	@POST(url = "/testMock02")
//	suspend fun testMock02(@Fields map: Map<String, String>): String
}