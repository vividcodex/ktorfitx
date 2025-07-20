package cn.ktorfitx.multiplatform.ksp.model

import com.google.devtools.ksp.symbol.KSValueParameter

internal class PathModel(
	val name: String,
	val varName: String,
	val parameter: KSValueParameter
)