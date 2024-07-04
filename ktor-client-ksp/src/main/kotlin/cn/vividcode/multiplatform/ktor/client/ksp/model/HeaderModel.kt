package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午4:02
 *
 * 介绍：HeaderModel
 */
internal data class HeaderModel(
	val name: String,
	override val varName: String,
	val encryptInfo: EncryptInfo?,
	override val className: ClassName
) : ValueParameterModel