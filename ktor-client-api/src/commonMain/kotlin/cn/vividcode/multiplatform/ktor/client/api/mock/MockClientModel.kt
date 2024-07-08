package cn.vividcode.multiplatform.ktor.client.api.mock

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/7 下午6:38
 *
 * 介绍：MockClientModel
 */
data class MockClientModel internal constructor(
	val headers: Map<String, Any>,
	val queries: Map<String, Any>,
	val forms: Map<String, Any>,
	val paths: Map<String, Any>,
	val body: String?
)