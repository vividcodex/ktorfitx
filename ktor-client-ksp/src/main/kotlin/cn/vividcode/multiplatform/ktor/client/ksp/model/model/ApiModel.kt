package cn.vividcode.multiplatform.ktor.client.ksp.model.model

import cn.vividcode.multiplatform.ktor.client.ksp.model.RequestMethod

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午2:07
 *
 * 介绍：ApiModel
 */
internal data class ApiModel(
	val requestMethod: RequestMethod,
	val url: String,
	val auth: Boolean
): FunctionModel