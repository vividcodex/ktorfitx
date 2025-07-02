package cn.ktorfitx.multiplatform.ksp.visitor

import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure

/**
 * Visitor 结果
 */
@JvmInline
internal value class VisitorResult(
	val classStructure: ClassStructure,
)