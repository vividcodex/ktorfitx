package cn.ktorfitx.multiplatform.sample.http

import cn.ktorfitx.multiplatform.core.ktorfit
import cn.ktorfitx.multiplatform.core.scope.ApiScope
import cn.ktorfitx.multiplatform.mock.config.mockClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val testKtorfit = ktorfit<TestApiScope> {
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

sealed interface TestApiScope : ApiScope