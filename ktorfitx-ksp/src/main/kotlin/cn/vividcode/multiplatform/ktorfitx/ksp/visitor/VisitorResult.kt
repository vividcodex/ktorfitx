package cn.vividcode.multiplatform.ktorfitx.ksp.visitor

import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ClassStructure

/**
 * Visitor 结果
 */
internal data class VisitorResult(
	val classStructure: ClassStructure,
)