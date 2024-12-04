package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

import com.google.devtools.ksp.symbol.KSValueParameter

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 16:14
 *
 * 文件介绍：PathModel
 */
internal class PathModel(
	val name: String,
	override val varName: String,
	val valueParameter: KSValueParameter
) : ValueParameterModel