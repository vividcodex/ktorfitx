package cn.vividcode.multiplatform.ktorfitx.api

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

/**
 * HttpClientEngine
 */
internal actual val HttpClientEngineFactory: HttpClientEngineFactory<*> by lazy { Js }