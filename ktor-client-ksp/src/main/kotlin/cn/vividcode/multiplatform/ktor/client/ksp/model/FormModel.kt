package cn.vividcode.multiplatform.ktor.client.ksp.model

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/12 下午3:55
 *
 * 介绍：FormModel
 */
internal data class FormModel(
	val name: String,
	val variableName: String,
	val sha256Layer: Int
)