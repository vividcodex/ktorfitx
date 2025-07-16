package cn.ktorfitx.multiplatform.ksp.model.structure

import com.squareup.kotlinpoet.TypeName

internal class ReturnStructure(
	val typeName: TypeName,
	val returnKind: ReturnKind
)

internal enum class ReturnKind {
	Unit,
	Result,
	Any
}