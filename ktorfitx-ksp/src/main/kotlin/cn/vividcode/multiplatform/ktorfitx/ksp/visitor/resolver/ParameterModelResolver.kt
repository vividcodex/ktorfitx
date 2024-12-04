package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithParameterAnnotationCountAndFormat
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/17 22:47
 *
 * 文件介绍：ParameterModelResolver
 */
internal object ParameterModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<ParameterModel> {
		return this.parameters.map {
			val funName = this.simpleName.asString()
			it.checkWithParameterAnnotationCountAndFormat(funName)
			val varName = it.name!!.asString()
			val typeName = it.type.toTypeName()
			ParameterModel(varName, typeName)
		}
	}
}