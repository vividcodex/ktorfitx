package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Body
import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithBodySize
import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithBodyType
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
		this.checkWithBodySize(valueParameters)
		val valueParameter = valueParameters.first()
		val varName = valueParameter.name!!.asString()
		val qualifiedName = valueParameter.type.resolve().declaration.qualifiedName?.asString()
		this.checkWithBodyType(qualifiedName)
		return BodyModel(varName, qualifiedName)
	}
}