package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.isLowerCamelCase
import cn.ktorfitx.common.ksp.util.expends.toLowerCamelCase
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.ParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

internal object ParameterModelResolver {
	
	private val annotationQualifiedNames = arrayOf(
		ClassNames.Body.canonicalName,
		ClassNames.Part.canonicalName,
		ClassNames.Field.canonicalName,
		ClassNames.Header.canonicalName,
		ClassNames.Path.canonicalName,
		ClassNames.Query.canonicalName
	)
	
	fun KSFunctionDeclaration.resolves(isWebSocket: Boolean): List<ParameterModel> {
		return if (isWebSocket) {
			val errorMessage = {
				"${simpleName.asString()} 方法上必须只使用 WebSocketSessionHandler 类型"
			}
			this.compileCheck(
				value = this.parameters.size == 1,
				errorMessage = errorMessage
			)
			val valueParameter = this.parameters.first()
			val typeName = valueParameter.type.toTypeName()
			this.compileCheck(
				value = typeName == ClassNames.WebSocketSessionHandler,
				errorMessage = errorMessage
			)
			val varName = valueParameter.name!!.asString()
			return listOf(ParameterModel(varName, typeName))
		} else {
			this.parameters.map { valueParameter ->
				val varName = valueParameter.name!!.asString()
				val annotationCount = valueParameter.annotations.toList()
					.map { it.annotationType.resolve().declaration.qualifiedName?.asString() }
					.count { it in annotationQualifiedNames }
				this.compileCheck(annotationCount > 0) {
					"${simpleName.asString()} 方法上的 $varName 参数未使用任何功能注解"
				}
				this.compileCheck(annotationCount == 1) {
					val useAnnotations = this.annotations.joinToString()
					"${simpleName.asString()} 方法上的 $varName 参数不允许同时使用 $useAnnotations 多个注解"
				}
				this.compileCheck(varName.isLowerCamelCase()) {
					val varNameSuggestion = varName.toLowerCamelCase()
					"${simpleName.asString()} 方法上的 $varName 参数不符合小驼峰命名规则，建议修改为 $varNameSuggestion"
				}
				val typeName = valueParameter.type.toTypeName()
				ParameterModel(varName, typeName)
			}
		}
	}
}