package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/6 上午9:42
 *
 * 介绍：ParameterModelResolver
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