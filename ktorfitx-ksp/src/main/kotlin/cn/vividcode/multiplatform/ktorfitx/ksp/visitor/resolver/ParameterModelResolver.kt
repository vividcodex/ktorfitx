package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isLowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.lowerCamelCase
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
	
	private val annotationQualifiedNames = arrayOf(
		KtorfitxQualifiers.BODY,
		KtorfitxQualifiers.FORM,
		KtorfitxQualifiers.HEADER,
		KtorfitxQualifiers.PATH,
		KtorfitxQualifiers.QUERY
	)
	
	fun KSFunctionDeclaration.resolves(): List<ParameterModel> {
		return this.parameters.map { valueParameter ->
			val varName = valueParameter.name!!.asString()
			val annotationCount = valueParameter.annotations.toList()
				.map { it.annotationType.resolve().declaration.qualifiedName?.asString() }
				.count { it in annotationQualifiedNames }
			this.compileCheck(annotationCount > 0) {
				val funName = this.simpleName.asString()
				"$funName 方法上 $varName 参数未使用任何功能注解"
			}
			this.compileCheck(annotationCount == 1) {
				val funName = this.simpleName.asString()
				val useAnnotations = this.annotations.joinToString()
				"$funName 方法上 $varName 参数不允许同时使用 $useAnnotations 多个注解"
			}
			this.compileCheck(varName.isLowerCamelCase()) {
				val funName = this.simpleName.asString()
				val varNameSuggestion = varName.lowerCamelCase()
				"$funName 方法上 $varName 参数不符合小驼峰命名规则，建议修改为 $varNameSuggestion"
			}
			val typeName = valueParameter.type.toTypeName()
			ParameterModel(varName, typeName)
		}
	}
}