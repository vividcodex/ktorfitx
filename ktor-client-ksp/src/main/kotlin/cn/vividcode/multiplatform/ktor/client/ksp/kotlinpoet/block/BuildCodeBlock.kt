package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktor.client.ksp.expends.classNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/4 下午11:31
 *
 * 介绍：BuildCodeBlock
 */
internal sealed class BuildCodeBlock(
	private val addImport: (String, Array<out String>) -> Unit
) {
	
	protected fun addImport(packageName: String, vararg simpleNames: String) {
		this.addImport(packageName, simpleNames)
	}
	
	protected fun addImport(typeName: TypeName) {
		when (typeName) {
			is ClassName -> addImport(typeName.packageName, typeName.simpleName)
			is ParameterizedTypeName -> addImports(typeName.classNames)
			else -> {}
		}
	}
	
	protected fun addImports(classNames: List<ClassName>) {
		classNames.forEach {
			addImport(it.packageName, it.simpleName)
		}
	}
}