package cn.vividcode.multiplatform.ktor.client.ksp.model

import cn.vividcode.multiplatform.ktor.client.api.encrypt.EncryptType

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午3:57
 *
 * 介绍：EncryptInfo
 */
internal data class EncryptInfo(
	val encryptType: EncryptType,
	val layer: Int
)