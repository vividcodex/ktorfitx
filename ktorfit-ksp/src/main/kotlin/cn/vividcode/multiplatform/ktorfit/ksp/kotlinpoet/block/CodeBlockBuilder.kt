package cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfit.ksp.expends.classNames
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/4 23:31
 *
 * 文件介绍：CodeBlockBuilder
 */
internal sealed class CodeBlockBuilder(
	private val funStructure: FunStructure,
	private val addImport: (String, Array<out String>) -> Unit
) {
	
	abstract fun CodeBlock.Builder.buildCodeBlock()
	
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
	
	protected fun getVarName(varName: String): String {
		var realVarName = varName
		var num = 1
		while (funStructure.valueParameterModels.any { it.varName == varName }) {
			realVarName = varName + num++
		}
		return realVarName
	}
}