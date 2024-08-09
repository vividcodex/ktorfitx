package cn.vividcode.multiplatform.ktorfit.api.mock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/7 18:38
 *
 * 文件介绍：MockClientModel
 */
data class MockClientModel internal constructor(
	val headers: Map<String, Any>,
	val queries: Map<String, Any>,
	val forms: Map<String, Any>,
	val paths: Map<String, Any>,
	val body: String?
)