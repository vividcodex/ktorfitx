package cn.ktorfitx.multiplatform.ksp.model.model

import com.squareup.kotlinpoet.TypeName

internal class BodyModel(
	override val varName: String,
	val typeName: TypeName,
) : ValueParameterModel