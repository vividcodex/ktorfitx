package cn.vividcode.multiplatform.ktorfit.ksp.model

import cn.vividcode.multiplatform.ktorfit.annotation.EncryptType

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 15:57
 *
 * 文件介绍：EncryptInfo
 */
internal data class EncryptInfo(
	val encryptType: EncryptType,
	val layer: Int
)