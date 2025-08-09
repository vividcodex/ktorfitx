package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.Field
import cn.ktorfitx.multiplatform.annotation.POST

@Api(url = "system")
interface SystemApi {
	
	@POST(url = "user/register")
	suspend fun register(
		@Field username: String,
		@Field password: String,
		@Field(name = "code") code: Int
	): Boolean
}