package cn.vividcode.multiplatform.ktorfitx.ksp.visitor

import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ClassStructure

/**
 * Visitor 结果
 */
@JvmInline
internal value class VisitorResult(
	val classStructure: ClassStructure,
)