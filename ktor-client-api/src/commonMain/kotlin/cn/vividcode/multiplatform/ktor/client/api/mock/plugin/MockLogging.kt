package cn.vividcode.multiplatform.ktor.client.api.mock.plugin

import cn.vividcode.multiplatform.ktor.client.api.mock.MockClientModel
import cn.vividcode.multiplatform.ktor.client.api.mock.MockDsl
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/7 上午4:07
 *
 * 介绍：MockLogging
 */
@MockDsl
sealed interface MockLogging {
	
	val baseUrl: String
	
	val logLevel: LogLevel
	
	val handleLog: (message: String) -> Unit
	
	val json: Json
	
	@MockDsl
	sealed interface Config {
		
		var baseUrl: String
		
		var handleLog: (message: String) -> Unit
		
		var logLevel: LogLevel
		
		var json: Json
	}
	
	private class ConfigImpl : Config {
		
		override var baseUrl: String = ""
		
		override var handleLog: (message: String) -> Unit = ::println
		
		override var logLevel = LogLevel.HEADERS
		
		override var json: Json = Json
	}
	
	companion object : MockClientPlugin<Config, MockLogging> {
		
		override fun install(block: Config.() -> Unit): MockLogging {
			val config = ConfigImpl().apply(block)
			return MockLoggingImpl(config.baseUrl, config.logLevel, config.handleLog, config.json)
		}
	}
}

private class MockLoggingImpl(
	override val baseUrl: String,
	override val logLevel: LogLevel,
	override val handleLog: (message: String) -> Unit,
	override val json: Json
) : MockLogging

@MockDsl
internal fun MockLogging.loggingRequest(
	httpMethod: HttpMethod,
	url: String,
	name: String,
	model: MockClientModel
) {
	val message = buildString {
		if (logLevel.info) {
			appendLine("REQUEST: $baseUrl$url")
			appendLine("MOCK NAME: $name")
			appendLine("METHOD: ${httpMethod.value}")
		}
		if (logLevel.headers && model.headers.isNotEmpty()) {
			appendLine("HEADERS [${model.headers.size}]")
			model.headers.forEach { (name, value) ->
				appendLine("-> $name: $value")
			}
		}
		if (logLevel.body) {
			if (model.forms.isNotEmpty()) {
				appendLine("FORMS [${model.forms.size}]")
				model.forms.forEach { (name, value) ->
					appendLine("-> $name: $value")
				}
			}
			if (model.body != null) {
				appendLine("BODY START [${model.body.length}]")
				append(model.body)
				append("BODY END")
			}
		}
	}
	this.handleLog(message)
}

inline fun <reified T : Any> MockLogging.loggingResponse(
	url: String,
	name: String,
	delay: Duration,
	mock: T
) {
	val message = buildString {
		if (logLevel.info) {
			appendLine("RESPONSE: $baseUrl$url")
			appendLine("MOCK NAME: $name")
			appendLine("DELAY TIME: $delay")
		}
		if (logLevel.body) {
			val json = json.encodeToString(mock)
			appendLine("BODY START [${json.length}]")
			appendLine(json)
			appendLine("BODY END")
		}
	}
	this.handleLog(message)
}