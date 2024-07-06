package cn.vividcode.multiplatform.ktor.client.ksp.model.model

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午4:02
 *
 * 介绍：HeadersModel
 */
internal data class HeadersModel(
	val headerMap: Map<String, String>,
) : FunctionModel