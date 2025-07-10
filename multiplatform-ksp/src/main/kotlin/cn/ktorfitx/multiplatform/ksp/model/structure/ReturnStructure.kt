package cn.ktorfitx.multiplatform.ksp.model.structure

import com.squareup.kotlinpoet.TypeName

internal sealed interface ReturnStructure

internal class AnyReturnStructure(
	val typeName: TypeName,
	val isResult: Boolean,
	val isUnit: Boolean
) : ReturnStructure

internal object UnitReturnStructure : ReturnStructure