package cn.vividcode.multiplatform.ktor.client.ksp.model.model

import cn.vividcode.multiplatform.ktor.client.ksp.model.EncryptInfo

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午3:56
 *
 * 介绍：FormModel
 */
internal data class FormModel(
	val name: String,
	override val varName: String,
	val encryptInfo: EncryptInfo?
) : ValueParameterModel