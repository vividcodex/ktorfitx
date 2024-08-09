package cn.vividcode.multiplatform.ktorfit.api.mock.plugin

import cn.vividcode.multiplatform.ktorfit.api.annotation.BuilderDsl
import cn.vividcode.multiplatform.ktorfit.api.mock.MockClientModel
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/7 4:07
 *
 * 文件介绍：MockLogging
 */
@BuilderDsl
sealed interface MockLogging {
	
	val baseUrl: String
	
	val logLevel: LogLevel
	
	val handleLog: (message: String) -> Unit
	
	val json: Json
	
	@BuilderDsl
	sealed interface Config {
		
		var baseUrl: String
		
		var logLevel: LogLevel
		
		var handleLog: (message: String) -> Unit
		
		var json: Json
	}
	
	private data class ConfigImpl(
		override var baseUrl: String = "",
		override var logLevel: LogLevel = LogLevel.HEADERS,
		override var handleLog: (message: String) -> Unit = {},
		override var json: Json = Json
	) : Config
	
	companion object : MockClientPlugin<Config, MockLogging> {
		
		override fun install(block: Config.() -> Unit): MockLogging {
			val (baseUrl, logLevel, handleLog, json) = ConfigImpl().apply(block)
			return MockLoggingImpl(baseUrl, logLevel, handleLog, json)
		}
	}
}

private class MockLoggingImpl(
	override val baseUrl: String,
	override val logLevel: LogLevel,
	override val handleLog: (message: String) -> Unit,
	override val json: Json
) : MockLogging

@BuilderDsl
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
	this.handleLog.invoke(message)
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
	this.handleLog.invoke(message)
}