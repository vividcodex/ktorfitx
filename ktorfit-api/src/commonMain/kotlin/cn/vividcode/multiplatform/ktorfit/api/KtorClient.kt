package cn.vividcode.multiplatform.ktorfit.api

import cn.vividcode.multiplatform.ktorfit.api.builder.KtorClientBuilder
import cn.vividcode.multiplatform.ktorfit.api.builder.KtorClientBuilderDsl
import cn.vividcode.multiplatform.ktorfit.api.builder.KtorClientBuilderDslImpl
import cn.vividcode.multiplatform.ktorfit.api.builder.KtorClientBuilderImpl
import cn.vividcode.multiplatform.ktorfit.api.config.HttpConfig
import cn.vividcode.multiplatform.ktorfit.api.config.KtorConfig
import cn.vividcode.multiplatform.ktorfit.api.config.MockConfig
import cn.vividcode.multiplatform.ktorfit.api.mock.MockClient
import cn.vividcode.multiplatform.ktorfit.api.mock.plugin.MockCache
import cn.vividcode.multiplatform.ktorfit.api.mock.plugin.MockLogging
import cn.vividcode.multiplatform.ktorfit.scope.ApiScope
import cn.vividcode.multiplatform.ktorfit.scope.DefaultApiScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/9 23:52
 *
 * 文件介绍：KtorClient
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class KtorClient<AS : ApiScope> internal constructor(
    val ktorConfig: KtorConfig,
    httpConfig: HttpConfig,
    mockConfig: MockConfig,
    private val apiScope: AS
) {

    init {
        this.ktorConfig.check()
        httpConfig.check()
    }

    companion object {

        fun builder(): KtorClientBuilder<DefaultApiScope> = KtorClientBuilderImpl(DefaultApiScope)

        /**
         * ktorClient 的构造器
         */
        fun <AS : ApiScope> builder(apiScope: AS): KtorClientBuilder<AS> = KtorClientBuilderImpl(apiScope)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        this.prettyPrint = httpConfig.jsonConfig.prettyPrint
        this.prettyPrintIndent = httpConfig.jsonConfig.prettyPrintIndent
    }

    /**
     * HttpClient
     */
    val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            install(Logging) {
                this.logger = object : Logger {
                    override fun log(message: String) {
                        httpConfig.handleLog?.invoke(format(message, httpConfig.showApiScope))
                    }
                }
                this.level = httpConfig.logLevel
            }
            install(ContentNegotiation) {
                json(this@KtorClient.json)
            }
            install(HttpCookies)
            engine {
                endpoint {
                    this.connectTimeout = httpConfig.connectTimeout
                    this.socketTimeout = httpConfig.socketTimeout
                    this.keepAliveTime = httpConfig.keepAliveTime
                }
            }
        }
    }

    /**
     * MockClient
     */
    val mockClient: MockClient by lazy {
        MockClient {
            install(MockLogging) {
                this.baseUrl = ktorConfig.baseUrl
                this.logLevel = httpConfig.logLevel
                this.handleLog = {
                    httpConfig.handleLog?.invoke(format(it, httpConfig.showApiScope))
                }
                this.json = this@KtorClient.json
            }
            install(MockCache) {
                this.groupMocksMap = mockConfig.groupMocksMap
            }
        }
    }

    private fun format(message: String, showApiScope: Boolean): String {
        return message + if (showApiScope) " <[$apiScope]>" else ""
    }
}

/**
 * DefaultApiScope 的 ktorClient 构造器
 */
fun ktorClient(builder: KtorClientBuilderDsl.() -> Unit): KtorClient<DefaultApiScope> {
    return KtorClientBuilderDslImpl(DefaultApiScope)
        .apply(builder)
        .build()
}

/**
 * 自定义 ApiScope 的 ktorClient 构造器
 */
fun <AS : ApiScope> ktorClient(apiScope: AS, builder: KtorClientBuilderDsl.() -> Unit): KtorClient<AS> {
    return KtorClientBuilderDslImpl(apiScope)
        .apply(builder)
        .build()
}