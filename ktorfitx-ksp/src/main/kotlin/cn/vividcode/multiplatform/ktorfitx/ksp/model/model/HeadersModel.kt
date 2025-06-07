package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 16:02
 *
 * 文件介绍：HeadersModel
 */
@JvmInline
internal value class HeadersModel(
	val headerMap: Map<String, String>,
) : FunctionModel