package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isLowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.lowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
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
	
	private const val ENCRYPT_QUALIFIED_NAME = "cn.vividcode.multiplatform.ktorfitx.annotation.Encrypt"
	
	private val annotationQualifiedNames = arrayOf(
		"cn.vividcode.multiplatform.ktorfitx.annotation.Body",
		"cn.vividcode.multiplatform.ktorfitx.annotation.Form",
		"cn.vividcode.multiplatform.ktorfitx.annotation.Header",
		"cn.vividcode.multiplatform.ktorfitx.annotation.Path",
		"cn.vividcode.multiplatform.ktorfitx.annotation.Query",
		"cn.vividcode.multiplatform.ktorfitx.annotation.Encrypt"
	)
	
	fun KSFunctionDeclaration.resolves(): List<ParameterModel> {
		return this.parameters.map {
			val funName = this.simpleName.asString()
			val varName = it.name!!.asString()
			it.checkAnnotations(funName, varName)
			varName.checkVarName(funName)
			val typeName = it.type.toTypeName()
			ParameterModel(varName, typeName)
		}
	}
	
	private fun KSValueParameter.checkAnnotations(funName: String, varName: String) {
		val annotations = this.annotations.mapNotNull {
			val qualifiedName = it.annotationType.resolve().declaration.qualifiedName?.asString()
			if (qualifiedName in annotationQualifiedNames) qualifiedName else null
		}.toMutableSet()
		check(annotations.isNotEmpty()) {
			"$funName 方法的 $varName 参数未使用任何注解"
		}
		annotations.remove(ENCRYPT_QUALIFIED_NAME)
		check(annotations.isNotEmpty()) {
			"$funName 方法的 $varName 参数不允许只使用 @Encrypt 注解"
		}
		check(annotations.size == 1) {
			val useAnnotations = annotations.joinToString { "@${it.substringAfterLast(".")}" }
			"$funName 方法的 $varName 参数不允许同时使用 $useAnnotations 注解"
		}
	}
	
	private fun String.checkVarName(funName: String) {
		check(this.isLowerCamelCase()) {
			"$funName 方法的 $this 参数不符合小驼峰命名规则，建议修改为 ${this.lowerCamelCase()}"
		}
	}
}