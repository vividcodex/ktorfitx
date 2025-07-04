package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Authentication
import cn.ktorfitx.server.annotation.DELETE
import cn.ktorfitx.server.annotation.GET
import cn.ktorfitx.server.annotation.PUT
import cn.ktorfitx.server.sample.model.ApiResult
import io.ktor.server.routing.*

@GET(path = "test/test05")
fun RoutingContext.test05(): String {
	return ""
}

@Authentication
@GET(path = "test/test06")
fun RoutingContext.test06(): ApiResult<String> {
	return ApiResult(-1, "", null)
}

object Test7 {
	
	@Authentication
	@PUT(path = "/test07")
	fun RoutingContext.test07(): Int {
		return -1
	}
	
	object Test8 {
		
		@Authentication
		@DELETE(path = "/test08")
		fun RoutingContext.test08(): ApiResult<Nothing> {
			return ApiResult(-1, "", null)
		}
	}
}