package cn.ktorfitx.common.ksp.util.expends

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 获取 TypeName 的 rawType，ClassName 是自己，ParameterizedTypeName 是 this.rawType
 */
val TypeName.rawType: ClassName
	get() = when (this) {
		is ParameterizedTypeName -> {
			this.rawType.copy(this.isNullable, emptyList(), emptyMap())
		}
		
		is ClassName -> this
		else -> error("不允许使用 rawType 属性")
	}