package cn.ktorfitx.multiplatform.ksp.model.model

import com.squareup.kotlinpoet.TypeName

internal class AttributeModel(
	val name: String,
	override val varName: String,
	val typeName: TypeName
) : ValueParameterModel