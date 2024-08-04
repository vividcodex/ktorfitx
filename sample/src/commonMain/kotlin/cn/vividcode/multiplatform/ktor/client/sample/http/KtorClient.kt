package cn.vividcode.multiplatform.ktor.client.sample.http

import cn.vividcode.multiplatform.ktor.client.annotation.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.ktorClient
import cn.vividcode.multiplatform.ktor.client.sample.http.api.TestResponse
import io.ktor.client.plugins.logging.*

val testKtorClient by lazy {
	ktorClient(TestApiScope) {
		baseUrl {
			host = "127.0.0.1"
			port = 9000
			prefix = "api"
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

object TestApiScope : ApiScope