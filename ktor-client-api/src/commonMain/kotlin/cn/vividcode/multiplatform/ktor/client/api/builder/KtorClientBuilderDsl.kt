package cn.vividcode.multiplatform.ktor.client.api.builder

import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.annotation.BuilderDsl
import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MocksConfig
import cn.vividcode.multiplatform.ktor.client.api.builder.mock.MocksConfigImpl
import cn.vividcode.multiplatform.ktor.client.api.config.HttpConfig
import cn.vividcode.multiplatform.ktor.client.api.config.JsonConfig
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.api.config.MockConfig
import cn.vividcode.multiplatform.ktor.client.scope.ApiScope
import io.ktor.client.plugins.logging.*

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/6/28 16:01
 *
 * 文件介绍：KtorClientBuilderDSL
 */
@BuilderDsl
sealed interface KtorClientBuilderDsl {
	
	/**
	 * baseUrl
	 */
	var baseUrl: String
	
	/**
	 * baseUrl
	 */
	fun baseUrl(block: BaseUrlBuilderDsl.() -> Unit)
	
	/**
	 * token
	 */
	fun token(token: () -> String)
	
	/**
	 * mocks
	 */
	fun mocks(block: MocksConfig.() -> Unit)
	
	/**
	 * connectTimeout
	 */
	var connectTimeout: Long
	
	/**
	 * socketTimeout
	 */
	var socketTimeout: Long
	
	/**
	 * keepAliveTime
	 */
	var keepAliveTime: Long
	
	/**
	 * logLevel
	 */
	var logLevel: LogLevel
	
	/**
	 * handleLog
	 */
	fun handleLog(handleLog: (String) -> Unit)
	
	/**
	 * showApiScope
	 */
	var showApiScope: Boolean
	
	/**
	 * json
	 */
	fun json(block: JsonDsl.() -> Unit)
}

internal class KtorClientBuilderDslImpl<AS : ApiScope>(
	private val apiScope: AS
) : KtorClientBuilderDsl {
	
	private val ktorConfig by lazy { KtorConfig() }
	private val httpConfig by lazy { HttpConfig() }
	private val mockConfig by lazy { MockConfig() }
	
	override var baseUrl: String = ktorConfig.baseUrl
		set(value) {
			field = value
			this.ktorConfig.baseUrl = baseUrl
		}
	
	override fun baseUrl(block: BaseUrlBuilderDsl.() -> Unit) {
		this.ktorConfig.baseUrl = BaseUrlBuilderDslImpl()
			.apply(block)
			.build()
	}
	
	override fun token(token: () -> String) {
		this.ktorConfig.token = token
	}
	
	override fun mocks(block: MocksConfig.() -> Unit) {
		val groupMocksMap = MocksConfigImpl().apply(block).groupMocksMap
		this.mockConfig.addGroupMocksMap(groupMocksMap)
	}
	
	override var connectTimeout: Long = httpConfig.connectTimeout
		set(value) {
			field = value
			this.httpConfig.connectTimeout = value
		}
	
	override var socketTimeout: Long = httpConfig.socketTimeout
		set(value) {
			field = value
			this.httpConfig.connectTimeout = value
		}
	
	override var keepAliveTime: Long = httpConfig.keepAliveTime
		set(value) {
			field = value
			this.httpConfig.keepAliveTime = value
		}
	
	override var logLevel: LogLevel = httpConfig.logLevel
		set(value) {
			field = value
			this.httpConfig.logLevel = value
		}
	
	override fun handleLog(handleLog: (String) -> Unit) {
		this.httpConfig.handleLog = handleLog
	}
	
	override var showApiScope: Boolean = false
		set(value) {
			field = value
			this.httpConfig.showApiScope = value
		}
	
	override fun json(block: JsonDsl.() -> Unit) {
		val jsonDsl = JsonDslImpl().apply(block)
		this.httpConfig.jsonConfig = JsonConfig(jsonDsl.prettyPrint, jsonDsl.prettyPrintIndent)
	}
	
	internal fun build(): KtorClient<AS> = KtorClient(ktorConfig, httpConfig, mockConfig, apiScope)
}

@BuilderDsl
sealed interface BaseUrlBuilderDsl {
	
	var host: String
	
	var port: Int
	
	var safe: Boolean
	
	var prefix: String
}

private class BaseUrlBuilderDslImpl : BaseUrlBuilderDsl {
	
	override var host: String = "localhost"
	
	override var port: Int = 80
	
	override var safe: Boolean = false
	
	override var prefix: String = ""
	
	fun build(): String = buildString {
		append(if (safe) "https://" else "http://")
		append(host)
		append(':')
		append(port)
		append(if (prefix.startsWith('/')) prefix else "/$prefix")
	}
}

@BuilderDsl
sealed interface JsonDsl {
	
	var prettyPrint: Boolean
	
	var prettyPrintIndent: String
}

private class JsonDslImpl : JsonDsl {
	
	override var prettyPrint: Boolean = false
	
	override var prettyPrintIndent: String = "    "
}