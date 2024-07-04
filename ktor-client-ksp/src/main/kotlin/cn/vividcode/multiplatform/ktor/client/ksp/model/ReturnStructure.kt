package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午11:04
 *
 * 介绍：ReturnStructure
 */
internal data class ReturnStructure(
	val className: ClassName,
	val parameterizes: List<ClassName> = emptyList()
) {
	
	constructor(className: ClassName, vararg parameterizes: ClassName) : this(className, parameterizes.toList())
	
	val imports: Map<String, Set<String>> by lazy {
		buildMap {
			this[className.packageName] = className.simpleNames.toSet()
			parameterizes.forEach {
				val simpleNames = this.getOrPut(it.packageName) { setOf() }
				this[it.packageName] = simpleNames + it.simpleNames
			}
		}
	}
	
	/**
	 * type属性
	 */
	val typeName: TypeName by lazy {
		if (parameterizes.isEmpty()) {
			className
		} else if (parameterizes.size == 1) {
			className.parameterizedBy(parameterizes[0])
		} else if (parameterizes.size == 2) {
			className.parameterizedBy(parameterizes[0].parameterizedBy(parameterizes[1]))
		} else {
			error("parameterizes 最多存放两个泛型")
		}
	}
	
	/**
	 * 转换为 String
	 */
	override fun toString(): String {
		return buildString {
			append(className.simpleName)
			if (parameterizes.size == 1) {
				append("<")
				append(parameterizes[0].simpleName)
				if (parameterizes.size == 2) {
					append("<")
					append(parameterizes[1].simpleName)
					append(">")
				}
				append(">")
			}
		}
	}
}