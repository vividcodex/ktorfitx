package cn.vividcode.multiplatform.ktor.client.ksp.model.model

import com.squareup.kotlinpoet.TypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/6 上午9:41
 *
 * 介绍：ParameterModel
 */
internal data class ParameterModel(
	val varName: String,
	val typeName: TypeName
)