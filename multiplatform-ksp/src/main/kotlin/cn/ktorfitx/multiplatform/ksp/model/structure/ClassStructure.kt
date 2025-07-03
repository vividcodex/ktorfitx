package cn.ktorfitx.multiplatform.ksp.model.structure

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier

internal class ClassStructure(
	val className: ClassName,
	val superinterface: ClassName,
	val kModifier: KModifier,
	val apiStructure: ApiStructure,
	val funStructures: List<FunStructure>,
)