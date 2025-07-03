package cn.ktorfitx.multiplatform.ksp.visitor

import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure

@JvmInline
internal value class VisitorResult(
	val classStructure: ClassStructure,
)