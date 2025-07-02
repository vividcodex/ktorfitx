package cn.vividcode.multiplatform.ktorfitx.sample.http

import cn.vividcode.multiplatform.ktorfitx.core.ktorfit
import cn.vividcode.multiplatform.ktorfitx.core.scope.ApiScope
import cn.vividcode.multiplatform.ktorfitx.mock.config.mockClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val testKtorfit = ktorfit(TestApiScope) {
	token { "<token>" }
	baseUrl = "http://localhost:8080/api/"
	httpClient(CIO) {
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
	mockClient {
	
	}
}

object TestApiScope : ApiScope