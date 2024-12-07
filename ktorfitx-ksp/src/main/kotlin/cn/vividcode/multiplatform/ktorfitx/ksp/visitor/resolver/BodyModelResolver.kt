package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Body
import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.BodyModel
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 13:25
 *
 * 文件介绍：BodyModelResolver
 */
internal object BodyModelResolver {
	
	@OptIn(KspExperimental::class)
	fun KSFunctionDeclaration.resolve(): BodyModel? {
		val valueParameters = this.parameters.filter { it.isAnnotationPresent(Body::class) }
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