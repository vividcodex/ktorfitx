package cn.ktorfitx.multiplatform.ksp.model.model

import com.google.devtools.ksp.symbol.KSValueParameter

internal class PathModel(
	val name: String,
	override val varName: String,
	val valueParameter: KSValueParameter
) : ValueParameterModel