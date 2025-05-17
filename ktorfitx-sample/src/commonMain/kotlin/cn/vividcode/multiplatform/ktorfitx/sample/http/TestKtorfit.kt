package cn.vividcode.multiplatform.ktorfitx.sample.http

import cn.vividcode.multiplatform.ktorfitx.api.ktorfit
import cn.vividcode.multiplatform.ktorfitx.api.scope.ApiScope
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val testKtorfit = ktorfit(TestApiScope) {
	token { "<token>" }
	httpClient(CIO) {
		defaultRequest {
			url("http://localhost:8080/api/")
		}
		engine {
			requestTimeout = 10_000L
			maxConnectionsCount = 200
		}
		install(ContentNegotiation) {
			json(
				Json {
					prettyPrint = true
					ignoreUnknownKeys = true
				}
			)
		}
	}
}

object TestApiScope : ApiScope