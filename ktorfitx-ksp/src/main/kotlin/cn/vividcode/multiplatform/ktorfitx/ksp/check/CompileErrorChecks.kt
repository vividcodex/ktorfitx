@file:OptIn(ExperimentalContracts::class)

package cn.vividcode.multiplatform.ktorfitx.ksp.check

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isLowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.lowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.kspLogger
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.BodyModel
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.FormModel
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ValueParameterModel
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSValueParameter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * `@Body` 数量错误提示
 */
internal fun KSFunctionDeclaration.checkWithBodySize(
	valueParameters: List<KSValueParameter>,
) {
	kspErrorCheck(valueParameters.size == 1) {
		COMPILE_ERROR_BODY_SIZE.format(this.simpleName.asString())
	}
}

/**
 * `@Body` 类型错误提示
 */
internal fun KSFunctionDeclaration.checkWithBodyType(
	qualifiedName: String?,
) {
	contract {
		returns() implies (qualifiedName != null)
	}
	kspErrorCheck(qualifiedName != null) {
		COMPILE_ERROR_BODY_TYPE.format(this.simpleName.asString())
	}
}

/**
 * 检查请求类型注解的数量
 */
internal fun KSFunctionDeclaration.checkWithRequestMethodCount(
	annotations: List<KSAnnotation>,
) {
	kspErrorCheck(annotations.isNotEmpty()) {
		val requestMethods = RequestMethod.entries.joinToString { "@${it.annotation.simpleName!!}" }
		COMPILE_ERROR_NOT_FOUND_REQUEST_METHOD.format(this.simpleName.asString(), requestMethods)
	}
	kspErrorCheck(annotations.size == 1) {
		COMPILE_ERROR_MULTIPLE_REQUEST_METHOD.format(
			this.simpleName.asString(),
			annotations.joinToString { "@${it.shortName.asString()}" },
			annotations.size
		)
	}
}

/**
 * 检查参数注解数量以及格式
 */
internal fun KSValueParameter.checkWithParameterAnnotationCountAndFormat(
	funName: String,
) {
	val qualifiedNames = arrayOf(
		KtorfitxQualifiers.BODY,
		KtorfitxQualifiers.FORM,
		KtorfitxQualifiers.HEADER,
		KtorfitxQualifiers.PATH,
		KtorfitxQualifiers.QUERY
	)
	val annotation = this.annotations.toList()
		.map { it.annotationType.resolve().declaration.qualifiedName?.asString() }
		.filter { it in qualifiedNames }
	val varName = this.name!!.asString()
	kspErrorCheck(annotation.isNotEmpty()) {
		COMPILE_ERROR_PARAMETER_NOT_FOUND_ANNOTATION.format(funName, varName)
	}
	kspErrorCheck(annotation.size == 1) {
		val useAnnotations = annotations.joinToString()
		COMPILE_ERROR_PARAMETER_MULTIPLE_ANNOTATIONS.format(funName, varName, useAnnotations)
	}
	kspErrorCheck(varName.isLowerCamelCase()) {
		COMPILE_ERROR_PARAMETER_VAR_NAME_FORMAT.format(funName, varName, varName.lowerCamelCase())
	}
}

private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()

/**
 * 检查 url 的格式
 */
internal fun KSFunctionDeclaration.checkWithUrlRegex(
	url: String,
	annotation: KSAnnotation,
) {
	kspErrorCheck(urlRegex.matches(url)) {
		COMPILE_ERROR_URL_REGEX_MESSAGE.format(this.simpleName.asString(), annotation)
	}
}

/**
 * 检查 同时使用 `@Body` 和 `@Form` 注解
 */
internal fun KSFunctionDeclaration.checkWithUseBothBodyAndForm(
	models: List<ValueParameterModel?>,
) {
	val value = arrayOf(BodyModel::class, FormModel::class).all { kClass -> models.any { kClass.isInstance(it) } }
	kspErrorCheck(!value) {
		COMPILE_USE_BOTH_BODY_AND_FORM.format(this.simpleName.asString())
	}
}

/**
 * 检查 `@Path` 注解的 `name` 没有在 url 上找到
 */
internal fun KSValueParameter.checkWithPathNotFound(
	url: String,
	name: String,
	funName: String,
	varName: String,
) {
	kspErrorCheck(url.contains("{$name}")) {
		COMPILE_ERROR_PATH_NOT_FOUND.format(funName, varName)
	}
}

//internal fun KSValueParameter.checkWith

/**
 * 检查错误，如果错误给用户提供错误点以及错误信息
 */
private inline fun KSNode.kspErrorCheck(
	value: Boolean,
	lazyMessage: () -> String,
) {
	if (!value) {
		kspLogger?.error(COMPILE_ERROR_KTORFITX.format(lazyMessage()), this)
	}
}