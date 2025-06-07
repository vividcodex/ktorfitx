package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.classNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import kotlin.concurrent.getOrSet

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/14 23:27
 *
 * 文件介绍：UseImports
 */
internal object UseImports {
	
	private val threadLocalImports = ThreadLocal<MutableMap<String, MutableSet<String>>>()
	
	fun get(): Map<String, Set<String>> {
		return this.threadLocalImports.get()
	}
	
	fun clear() {
		this.threadLocalImports.get()?.clear()
	}
	
	fun addImports(packageName: String, vararg simpleNames: String) {
		if (packageName == "kotlin") return
		this.threadLocalImports.getOrSet { mutableMapOf() }
			.getOrPut(packageName) { mutableSetOf() }
			.addAll(simpleNames)
	}
	
	private fun addImportsByClassNames(classNames: List<ClassName>) {
		classNames.forEach {
			val className = it.topLevelClassName()
			addImports(className.packageName, className.simpleName)
		}
	}
	
	private fun addImportByTypeName(typeName: TypeName) {
		when (typeName) {
			is ClassName -> {
				val className = typeName.topLevelClassName()
				addImports(className.packageName, className.simpleName)
			}
			is ParameterizedTypeName -> addImportsByClassNames(typeName.classNames)
			else -> {}
		}
	}
	
	operator fun plusAssign(typeName: TypeName) {
		this.addImportByTypeName(typeName)
	}
	
	operator fun plusAssign(classNames: List<ClassName>) {
		this.addImportsByClassNames(classNames)
	}
	
	operator fun plusAssign(qualifiedName: String) {
		this += ClassName.bestGuess(qualifiedName)
	}
}