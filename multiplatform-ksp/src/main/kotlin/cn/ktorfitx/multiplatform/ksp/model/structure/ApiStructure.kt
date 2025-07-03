package cn.ktorfitx.multiplatform.ksp.model.structure

import com.squareup.kotlinpoet.ClassName

internal class ApiStructure(
	val url: String?,
	val apiScopeClassNames: Set<ClassName>,
)