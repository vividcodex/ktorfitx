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

object TestApiScope : ApiScope {
	
	override val name: String = "测试作用域"
	
	override val description: String = "ktorfit 项目的 sample 模块的测试作用域"
}