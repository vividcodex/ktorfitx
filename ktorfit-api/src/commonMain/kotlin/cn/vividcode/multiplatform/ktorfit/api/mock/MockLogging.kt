package cn.vividcode.multiplatform.ktorfit.api.mock

import cn.vividcode.multiplatform.ktorfit.api.config.LogConfig
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/11 04:34
 *
 * 文件介绍：MockLogging
 */
class MockLogging(
	val log: LogConfig,
	private val mockRequest: MockRequestBuilder,
	urlString: String,
) {
	
	val urlString: String by lazy {
		formatUrl(urlString, mockRequest.paths, mockRequest.queries)
	}
	
	fun request(method: HttpMethod) {
		if (log.level != LogLevel.NONE) {
			buildString {
				if (log.level.info) {
					val url = formatUrl(urlString, mockRequest.paths, mockRequest.queries)
					appendLine("REQUEST: $url")
					appendLine("METHOD: ${method.value}")
				}
				if (log.level.headers && mockRequest.headers.isNotEmpty()) {
					appendLine("HEADERS [${mockRequest.headers.size}]")
					mockRequest.headers.forEach { (name, value) ->
						appendLine("-> $name: $value")
					}
				}
				if (log.level.body) {
					if (mockRequest.forms.isNotEmpty()) {
						appendLine("FORMS [${mockRequest.forms.size}]")
						mockRequest.forms.forEach { (name, value) ->
							appendLine("-> $name: $value")
						}
					}
					if (mockRequest.body != null) {
						appendLine("BODY START [${mockRequest.body!!.length}]")
						appendLine(mockRequest.body!!)
						appendLine("BODY END")
					}
				}
			}.let(log.logger)
		}
	}
	
	inline fun <reified T : Any> response(
		mock: T,
		json: Json,
		delay: Long
	) {
		if (log.level != LogLevel.NONE) {
			buildString {
				if (log.level.info) {
					appendLine("RESPONSE: $urlString")
					appendLine("DELAY TIME: $delay")
				}
				if (log.level.body) {
					val stringJson = json.encodeToString(mock)
					val length = stringJson.filterNot { it == ' ' || it == '\n' }.length
					appendLine("BODY START [$length]")
					appendLine(stringJson)
					appendLine("BODY END")
				}
			}.also(log.logger)
		}
	}
	
	private fun formatUrl(
		urlString: String,
		paths: Map<String, Any>,
		queries: Map<String, Any>
	): String = buildString {
		val url = paths.entries.fold(urlString) { acc, path ->
			acc.replace("{${path.key}}", path.value.toString())
		}
		append(url)
		queries.entries.forEachIndexed { index, query ->
			append(if (index == 0) '?' else '&')
			append(query.key)
			append('=')
			append(query.value)
		}
	}
}