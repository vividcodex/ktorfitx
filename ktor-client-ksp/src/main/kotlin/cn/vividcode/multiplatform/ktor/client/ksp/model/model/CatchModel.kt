package cn.vividcode.multiplatform.ktor.client.ksp.model.model

import com.squareup.kotlinpoet.TypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/5 下午12:23
 *
 * 介绍：CatchModel
 */
internal data class CatchModel(
	override val varName: String,
	val exceptionTypeName: TypeName
) : ValueParameterModel