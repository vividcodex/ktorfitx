package cn.vividcode.multiplatform.ktor.client.ksp.model

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/12 下午5:25
 *
 * 介绍：RequestTypeModel
 */
internal data class RequestTypeModel(
	val type: RequestType,
	val url: String,
	val auth: Boolean
)

enum class RequestType {
	GET,
	POST,
	PUT,
	DELETE
}