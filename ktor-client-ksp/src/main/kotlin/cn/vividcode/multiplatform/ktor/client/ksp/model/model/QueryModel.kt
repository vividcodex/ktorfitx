package cn.vividcode.multiplatform.ktor.client.ksp.model.model

import cn.vividcode.multiplatform.ktor.client.ksp.model.EncryptInfo

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 16:14
 *
 * 文件介绍：QueryModel
 */
internal data class QueryModel(
	val name: String,
	override val varName: String,
	val encryptInfo: EncryptInfo?,
) : ValueParameterModel