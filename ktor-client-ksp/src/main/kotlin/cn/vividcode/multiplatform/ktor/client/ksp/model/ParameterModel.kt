package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/12 下午6:32
 *
 * 介绍：ParameterModel
 */
internal data class ParameterModel(
	val name: String,
	val className: ClassName
)