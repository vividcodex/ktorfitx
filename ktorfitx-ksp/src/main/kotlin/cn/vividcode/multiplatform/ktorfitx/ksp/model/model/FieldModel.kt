package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2025/06/13 17:49
 *
 * 文件介绍：FieldModel
 */
internal class FieldModel(
	val name: String,
	override val varName: String,
	val isString: Boolean
) : ValueParameterModel