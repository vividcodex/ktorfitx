package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.ksp.model.model.ParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 9:42
 *
 * 文件介绍：ParameterModelResolver
 */
internal data object ParameterModelResolver {
	
	fun resolves(functionDeclaration: KSFunctionDeclaration): List<ParameterModel> {
		return functionDeclaration.parameters.map {
			val varName = it.name!!.asString()
			val typeName = it.type.resolve().toTypeName()
			ParameterModel(varName, typeName)
		}
	}
}