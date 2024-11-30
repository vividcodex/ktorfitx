package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 14:07
 *
 * 文件介绍：ApiModel
 */
internal data class ApiModel(
	val requestFunName: String,
	val url: String
) : FunctionModel