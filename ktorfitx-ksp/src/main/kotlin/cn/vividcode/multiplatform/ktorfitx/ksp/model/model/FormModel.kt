package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

import cn.vividcode.multiplatform.ktorfitx.ksp.model.EncryptInfo

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 15:56
 *
 * 文件介绍：FormModel
 */
internal data class FormModel(
	val name: String,
	override val varName: String,
	val encryptInfo: EncryptInfo?
) : ValueParameterModel