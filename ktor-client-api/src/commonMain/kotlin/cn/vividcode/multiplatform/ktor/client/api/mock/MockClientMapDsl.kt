package cn.vividcode.multiplatform.ktor.client.api.mock

import cn.vividcode.multiplatform.ktor.client.api.annotation.BuilderDsl

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/7 下午6:39
 *
 * 介绍：MockClientMapDsl
 */
@BuilderDsl
sealed interface MockClientMapDsl {
	
	val valueMap: MutableMap<String, Any>
	
}

internal class MockClientMapDslImpl : MockClientMapDsl {
	
	override val valueMap = mutableMapOf<String, Any>()
}

/**
 * append
 */
fun <T : Any> MockClientMapDsl.append(name: String, value: T) {
	this.valueMap[name] = value
}