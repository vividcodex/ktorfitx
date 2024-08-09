package cn.vividcode.multiplatform.ktor.client.ksp.model.model

import com.squareup.kotlinpoet.TypeName

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/5 12:23
 *
 * 文件介绍：CatchModel
 */
internal data class CatchModel(
	override val varName: String,
	val exceptionTypeName: TypeName
) : ValueParameterModel