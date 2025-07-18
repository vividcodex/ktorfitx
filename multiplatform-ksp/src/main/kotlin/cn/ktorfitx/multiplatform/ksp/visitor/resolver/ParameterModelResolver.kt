package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.common.ksp.util.expends.isLowerCamelCase
import cn.ktorfitx.common.ksp.util.expends.toLowerCamelCase
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.ParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName


private val parameterClassNames = arrayOf(
	TypeNames.Body,
	TypeNames.Part,
	TypeNames.Field,
	TypeNames.Header,
	TypeNames.Path,
	TypeNames.Query,
	TypeNames.Cookie,
	TypeNames.Attribute
)

internal fun KSFunctionDeclaration.resolveParameterModels(isWebSocket: Boolean): List<ParameterModel> {
	return if (isWebSocket) {
		val errorMessage = {
			"${simpleName.asString()} 函数只允许一个参数，且类型为 WebSocketSessionHandler 别名 或 suspend DefaultClientWebSocketSession.() -> Unit"
		}
		this.compileCheck(
			value = this.parameters.size == 1,
			errorMessage = errorMessage
		)
		val valueParameter = this.parameters.first()
		val typeName = valueParameter.type.toTypeName()
		this.compileCheck(
			value = typeName == TypeNames.WebSocketSessionHandler || typeName == TypeNames.DefaultClientWebSocketSessionLambda,
			errorMessage = errorMessage
		)
		val varName = valueParameter.name!!.asString()
		return listOf(ParameterModel(varName, typeName))
	} else {
		this.parameters.map { parameter ->
			val varName = parameter.name!!.asString()
			val count = parameterClassNames.count {
				parameter.hasAnnotation(it)
			}
			this.compileCheck(count > 0) {
				"${simpleName.asString()} 函数上的 $varName 参数未使用任何功能注解"
			}
			this.compileCheck(count == 1) {
				val useAnnotations = this.annotations.joinToString()
				"${simpleName.asString()} 函数上的 $varName 参数不允许同时使用 $useAnnotations 多个注解"
			}
			this.compileCheck(varName.isLowerCamelCase()) {
				val varNameSuggestion = varName.toLowerCamelCase()
				"${simpleName.asString()} 函数上的 $varName 参数不符合小驼峰命名规则，建议修改为 $varNameSuggestion"
			}
			val typeName = parameter.type.toTypeName()
			ParameterModel(varName, typeName)
		}
	}
}