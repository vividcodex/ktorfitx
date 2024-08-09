package cn.vividcode.multiplatform.ktorfit.sample.http

import cn.vividcode.multiplatform.ktorfit.api.ktorfit
import cn.vividcode.multiplatform.ktorfit.sample.http.api.TestResponse
import cn.vividcode.multiplatform.ktorfit.scope.ApiScope
import io.ktor.client.plugins.logging.*

val testKtorfit by lazy {
	ktorfit(TestApiScope) {
		baseUrls("default") {
			this["default"] = "http://localhost:9000/api"
		}
		handleLog {
			println(it)
		}
		token { "<TOKEN>" }
		logLevel = LogLevel.ALL
		json {
			prettyPrint = true
		}
		mocks {
			api("/test/testMock01") {
				mock(name = "mock01") {
					success(TestResponse("测试响应"))
				}
			}
		}
	}
}

object TestApiScope : ApiScope {
	
	override val name: String = "测试作用域"
	
	override val description: String = "ktorfit 项目的 sample 模块的测试作用域"
}