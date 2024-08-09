package cn.vividcode.multiplatform.ktor.client.ksp.model

import cn.vividcode.multiplatform.ktor.client.annotation.EncryptType

/**
 * 项目名称：vividcode-multiplatform-ktor-client
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