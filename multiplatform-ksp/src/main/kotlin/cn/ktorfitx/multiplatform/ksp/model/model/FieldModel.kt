package cn.ktorfitx.multiplatform.ksp.model.model

internal class FieldModel(
	val name: String,
	override val varName: String,
	val isStringType: Boolean
) : ValueParameterModel