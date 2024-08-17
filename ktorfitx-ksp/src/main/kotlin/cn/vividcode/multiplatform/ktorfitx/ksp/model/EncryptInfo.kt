package cn.vividcode.multiplatform.ktorfitx.ksp.model

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
	val encryptType: String,
	val hexType: String,
	val layer: Int
)