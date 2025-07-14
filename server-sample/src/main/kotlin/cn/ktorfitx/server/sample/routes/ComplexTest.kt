package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.*
import cn.ktorfitx.server.annotation.Authentication
import cn.ktorfitx.server.annotation.Principal
import io.ktor.http.content.*
import io.ktor.server.auth.*

@Authentication
@POST("complex/test1")
fun complexTest1(
	@Field field: String,
	@Principal principal: UserIdPrincipal,
	@Query query: String,
): String = ""

@Authentication
@POST("complex/test2")
fun complexTest2(
	@PartForm part: String,
	@PartForm("custom1") part2: String,
	@PartFile file: ByteArray?,
	@PartBinary binary: PartData.BinaryItem,
	@PartBinaryChannel binaryChannel: PartData.BinaryChannelItem,
	@Principal principal: UserIdPrincipal,
	@Query query: String,
	@Query("custom2") query2: Int
): String = ""