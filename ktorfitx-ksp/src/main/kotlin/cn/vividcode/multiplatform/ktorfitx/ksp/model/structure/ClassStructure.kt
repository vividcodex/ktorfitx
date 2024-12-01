package cn.vividcode.multiplatform.ktorfitx.ksp.model.structure

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 15:47
 *
 * 文件介绍：ClassStructure
 */
internal class ClassStructure(
	val className: ClassName,
	val superinterface: ClassName,
	val kModifier: KModifier,
	val apiStructure: ApiStructure,
	val funStructures: Sequence<FunStructure>,
)