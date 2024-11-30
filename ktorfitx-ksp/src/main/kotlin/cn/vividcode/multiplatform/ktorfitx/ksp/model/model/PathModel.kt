package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 16:14
 *
 * 文件介绍：PathModel
 */
internal data class PathModel(
	val name: String,
	override val varName: String,
) : ValueParameterModel