package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.*
import cn.ktorfitx.server.sample.model.ApiResult
import io.ktor.server.routing.*

@GET(path = "test/test05")
@Group("test02")
fun RoutingContext.test05(): String {
	return ""
}

@Authentication
@Group("test02")
@GET(path = "test/test06")
fun RoutingContext.test06(): ApiResult<String> {
	return ApiResult(-1, "", null)
}

object Test7 {
	
	@Authentication
	@Group("test02")
	@PUT(path = "/test07")
	fun RoutingContext.test07(): Int {
		return -1
	}
	
	object Test8 {
		
		@Authentication
		@Group("test02")
		@DELETE(path = "/test08")
		fun RoutingContext.test08(): ApiResult<Nothing> {
			return ApiResult(-1, "", null)
		}
	}
}