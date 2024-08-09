package cn.vividcode.multiplatform.ktorfit.api.builder

import cn.vividcode.multiplatform.ktorfit.api.KtorClient
import cn.vividcode.multiplatform.ktorfit.api.annotation.BuilderDsl
import cn.vividcode.multiplatform.ktorfit.api.builder.mock.MocksConfig
import cn.vividcode.multiplatform.ktorfit.api.builder.mock.MocksConfigImpl
import cn.vividcode.multiplatform.ktorfit.api.config.HttpConfig
import cn.vividcode.multiplatform.ktorfit.api.config.JsonConfig
import cn.vividcode.multiplatform.ktorfit.api.config.KtorConfig
import cn.vividcode.multiplatform.ktorfit.api.config.MockConfig
import cn.vividcode.multiplatform.ktorfit.scope.ApiScope
import io.ktor.client.plugins.logging.*

/**
 * 项目名称：vividcode-multiplatform-ktorfit
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
	 * baseUrls
	 */
	fun baseUrls(useKey: String, block: MutableMap<String, String>.() -> Unit)
	
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
			if (ktorConfig.baseUrl.isBlank()) {
				field = value
				this.ktorConfig.baseUrl = baseUrl
			}
		}
	
	override fun baseUrls(useKey: String, block: MutableMap<String, String>.() -> Unit) {
		if (ktorConfig.baseUrl.isBlank()) {
			val baseUrls = mutableMapOf<String, String>()
				.apply(block)
			this.ktorConfig.baseUrl = baseUrls[useKey] ?: "$useKey 没有定义"
		}
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
sealed interface JsonDsl {
	
	var prettyPrint: Boolean
	
	var prettyPrintIndent: String
}

private class JsonDslImpl : JsonDsl {
	
	override var prettyPrint: Boolean = false
	
	override var prettyPrintIndent: String = "    "
}