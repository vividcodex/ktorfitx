package cn.ktorfitx.multiplatform.ksp.model

import com.squareup.kotlinpoet.ClassName

internal class BodyModel(
	val varName: String,
	val formatClassName: ClassName
) : RequestBodyModel