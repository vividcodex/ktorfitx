package cn.ktorfitx.multiplatform.ksp.model.model

import com.squareup.kotlinpoet.ClassName

internal class MockModel(
	val provider: ClassName,
	val delay: Long
) : FunModel