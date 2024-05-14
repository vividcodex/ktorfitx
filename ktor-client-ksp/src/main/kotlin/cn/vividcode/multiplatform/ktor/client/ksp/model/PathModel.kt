package cn.vividcode.multiplatform.ktor.client.ksp.model

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 下午3:24
 *
 * 介绍：PathModel
 */
internal data class PathModel(
	val name: String,
	val variableName: String,
	val sha256Layer: Int
)