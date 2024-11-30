package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

import com.squareup.kotlinpoet.ClassName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/2 21:59
 *
 * 文件介绍：MockModel
 */
internal class MockModel(
	val provider: ClassName,
	val status: ClassName,
	val delayRange: Array<Long>
) : FunctionModel