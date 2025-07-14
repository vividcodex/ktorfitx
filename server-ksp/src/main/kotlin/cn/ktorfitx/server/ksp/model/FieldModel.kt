package cn.ktorfitx.server.ksp.model

import com.squareup.kotlinpoet.TypeName

internal class FieldModel(
	val name: String,
	val varName: String,
	val typeName: TypeName,
	val isNullable: Boolean,
)