package cn.vividcode.multiplatform.ktorfitx.sample.http

import cn.vividcode.multiplatform.ktorfitx.api.ktorfit
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import io.ktor.client.plugins.logging.*

val testKtorfit by lazy {
	ktorfit(TestApiScope) {
		baseUrl = "http://localhost:8080/api"
		token { "<token>" }
		log {
			level = LogLevel.ALL
			logger = ::println
		}
		json {
			prettyPrint = true
		}
	}
}

object TestApiScope : ApiScope