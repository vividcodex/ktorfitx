package cn.vividcode.multiplatform.ktor.client.ksp.model.model

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 14:07
 *
 * 文件介绍：ApiModel
 */
internal data class ApiModel(
	val requestFunName: String,
	val url: String,
	val auth: Boolean
) : FunctionModel