package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.BodyModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSFunctionDeclaration.resolveBodyModel(): BodyModel? {
	val valueParameters = this.parameters.filter {
		it.hasAnnotation(ClassNames.Body)
	}
	if (valueParameters.isEmpty()) return null
	this.compileCheck(valueParameters.size == 1) {
		"${simpleName.asString()} 函数不允许使用多个 @Body 注解"
	}
	val valueParameter = valueParameters.first()
	val varName = valueParameter.name!!.asString()
	val typeName = valueParameter.type.resolve().toTypeName()
	this.compileCheck(typeName is ClassName || typeName is ParameterizedTypeName) {
		"${simpleName.asString()} 函数的参数列表中标记了 @Body 注解，但是未找到参数类型"
	}
	return BodyModel(varName)
}