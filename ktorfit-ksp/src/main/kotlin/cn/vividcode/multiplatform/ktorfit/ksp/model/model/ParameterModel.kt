package cn.vividcode.multiplatform.ktorfit.ksp.model.model

import com.squareup.kotlinpoet.TypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 9:41
 *
 * 文件介绍：ParameterModel
 */
internal data class ParameterModel(
	val varName: String,
	val typeName: TypeName
)