package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.*
import cn.ktorfitx.server.sample.model.ApiResult
import cn.ktorfitx.server.sample.model.Custom
import io.ktor.server.routing.*

@GET(path = "/test01")
fun RoutingContext.test01(): String {
	return ""
}

@Authentication
@POST(path = "/test02")
fun RoutingContext.test02(): ApiResult<String> {
	return ApiResult(-1, "", null)
}

object Test3 {
	
	@Authentication
	@PUT(path = "/test03")
	fun RoutingContext.test03(): Int {
		return -1
	}
	
	object Test4 {
		
		@Authentication
		@DELETE(path = "/test04")
		fun RoutingContext.test04(): ApiResult<Custom> {
			return ApiResult(-1, "", Custom("xxx"))
		}
	}
}