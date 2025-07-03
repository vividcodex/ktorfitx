package cn.ktorfitx.common.ksp.util.expends

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 获取泛型类中的所有ClassName
 */
val ParameterizedTypeName.classNames: List<ClassName>
	get() = buildSet {
		this += rawType
		typeArguments.forEach {
			when (it) {
				is ClassName -> add(it)
				is ParameterizedTypeName -> addAll(it.classNames)
				else -> {}
			}
		}
	}.toList()

/**
 * 获取 TypeName 上的 simpleName
 */
val TypeName.simpleName: String
	get() = when (this) {
		is ParameterizedTypeName -> this.simpleName
		is ClassName -> this.simpleName
		else -> this.toString()
	}

private val ParameterizedTypeName.simpleName: String
	get() = buildString {
		append(rawType.simpleName)
		if (typeArguments.isNotEmpty()) {
			append("<")
			val code = typeArguments.joinToString {
				when (it) {
					is ClassName -> it.simpleName
					is ParameterizedTypeName -> it.simpleName
					else -> it.toString()
				}
			}
			append(code)
			append(">")
		}
	}

/**
 * 获取 TypeName 的 rawType
 */
val TypeName.rawType: ClassName
	get() = when (this) {
		is ParameterizedTypeName -> {
			this.rawType.copy(this.isNullable, emptyList(), emptyMap())
		}
		
		is ClassName -> this
		else -> error("不允许使用 rawType 属性")
	}