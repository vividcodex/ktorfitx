package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Body
import cn.vividcode.multiplatform.ktorfitx.ksp.messages.CompileErrorMessages
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
		check(valueParameters.size == 1) {
			CompileErrorMessages.bodySizeMessage(this.qualifiedName!!.asString())
		}
		return valueParameters.first().let {
			val varName = it.name!!.asString()
			val qualifiedName = it.type.resolve().declaration.qualifiedName?.asString()
			check(qualifiedName != null) {
				CompileErrorMessages.bodyTypeMessage(this.qualifiedName!!.asString())
			}
			BodyModel(varName, qualifiedName)
		}
	}
}