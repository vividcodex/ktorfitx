package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.BodyModel
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object BodyModelResolver {
	
	@OptIn(KspExperimental::class)
	fun KSFunctionDeclaration.resolve(): BodyModel? {
		val valueParameters = this.parameters.filter {
			it.hasAnnotation(ClassNames.Body)
		}
		if (valueParameters.isEmpty()) return null
		this.compileCheck(valueParameters.size == 1) {
			"${this.simpleName.asString()} 方法不允许使用多个 @Body 注解"
		}
		val valueParameter = valueParameters.first()
		val varName = valueParameter.name!!.asString()
		val qualifiedName = valueParameter.type.resolve().declaration.qualifiedName?.asString()
		this.compileCheck(qualifiedName != null) {
			"${this.simpleName.asString()} 方法的参数列表中标记了 @Body 注解，但是未找到参数类型"
		}
		return BodyModel(varName, qualifiedName)
	}
}