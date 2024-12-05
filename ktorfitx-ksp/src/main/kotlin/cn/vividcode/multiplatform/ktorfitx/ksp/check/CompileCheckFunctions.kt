@file:OptIn(ExperimentalContracts::class)

package cn.vividcode.multiplatform.ktorfitx.ksp.check

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.code
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getArgumentKSClassDeclaration
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isLowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.lowerCamelCase
import cn.vividcode.multiplatform.ktorfitx.ksp.kspLogger
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.BodyModel
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.FormModel
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ValueParameterModel
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * `@Body` 数量错误提示
 */
internal fun KSFunctionDeclaration.checkWithBodySize(
	valueParameters: List<KSValueParameter>,
) {
	compileErrorCheck(valueParameters.size == 1) {
		COMPILE_MESSAGE_BODY_SIZE.format(this.simpleName.asString())
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
	compileErrorCheck(qualifiedName != null) {
		COMPILE_MESSAGE_BODY_TYPE.format(this.simpleName.asString())
	}
}

/**
 * 检查请求类型注解的数量
 */
internal fun KSFunctionDeclaration.checkWithRequestMethodCount(
	annotations: List<KSAnnotation>,
) {
	compileErrorCheck(annotations.isNotEmpty()) {
		val requestMethods = RequestMethod.entries.joinToString { "@${it.annotation.simpleName!!}" }
		COMPILE_MESSAGE_NOT_FOUND_REQUEST_METHOD.format(this.simpleName.asString(), requestMethods)
	}
	compileErrorCheck(annotations.size == 1) {
		COMPILE_MESSAGE_MULTIPLE_REQUEST_METHOD.format(
			this.simpleName.asString(),
			annotations.joinToString { "@${it.shortName.asString()}" },
			annotations.size
		)
	}
}

private val annotationQualifiedNames by lazy {
	arrayOf(
		KtorfitxQualifiers.BODY,
		KtorfitxQualifiers.FORM,
		KtorfitxQualifiers.HEADER,
		KtorfitxQualifiers.PATH,
		KtorfitxQualifiers.QUERY
	)
}

/**
 * 检查参数注解数量以及格式
 */
internal fun KSValueParameter.checkWithParameterAnnotationCountAndFormat(
	funName: String,
) {
	val annotation = this.annotations.toList()
		.map { it.annotationType.resolve().declaration.qualifiedName?.asString() }
		.filter { it in annotationQualifiedNames }
	val varName = this.name!!.asString()
	compileErrorCheck(annotation.isNotEmpty()) {
		COMPILE_MESSAGE_PARAMETER_NOT_FOUND_ANNOTATION.format(funName, varName)
	}
	compileErrorCheck(annotation.size == 1) {
		val useAnnotations = annotations.joinToString()
		COMPILE_MESSAGE_PARAMETER_MULTIPLE_ANNOTATIONS.format(funName, varName, useAnnotations)
	}
	compileErrorCheck(varName.isLowerCamelCase()) {
		COMPILE_MESSAGE_PARAMETER_VAR_NAME_FORMAT.format(funName, varName, varName.lowerCamelCase())
	}
}

private val urlRegex by lazy {
	"^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
}

/**
 * 检查 url 的格式
 */
internal fun KSFunctionDeclaration.checkWithUrlRegex(
	url: String,
	annotation: KSAnnotation,
) {
	compileErrorCheck(urlRegex.matches(url)) {
		COMPILE_MESSAGE_URL_REGEX_MESSAGE.format(this.simpleName.asString(), annotation)
	}
}

/**
 * 检查 同时使用 `@Body` 和 `@Form` 注解
 */
internal fun KSFunctionDeclaration.checkWithUseBothBodyAndForm(
	models: List<ValueParameterModel?>,
) {
	val value = arrayOf(BodyModel::class, FormModel::class).all { kClass -> models.any { kClass.isInstance(it) } }
	compileErrorCheck(!value) {
		COMPILE_MESSAGE_USE_BOTH_BODY_AND_FORM.format(this.simpleName.asString())
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
	compileErrorCheck(url.contains("{$name}")) {
		COMPILE_MESSAGE_PATH_NOT_FOUND.format(funName, varName)
	}
}

private val mockProviderClassName by lazy {
	ClassName.bestGuess(KtorfitxQualifiers.MOCK_PROVIDER)
}

/**
 * 检查 `@Mock` 注解的 provider 参数类型
 */
internal fun KSAnnotation.checkWithMockProviderType(
	mockProvider: ClassName,
	funName: String,
) {
	compileErrorCheck(mockProvider != mockProviderClassName) {
		COMPILE_MESSAGE_UNUSABLE_MOCK_PROVIDER.format(funName)
	}
	val classDeclaration = this.getArgumentKSClassDeclaration("provider")!!
	val classKind = classDeclaration.classKind
	classDeclaration.compileErrorCheck(classKind == ClassKind.OBJECT) {
		val className = classDeclaration.simpleName.asString()
		COMPILE_MESSAGE_REALIZE_MOCK_PROVIDER_TYPE.format(className, classKind.code)
	}
}

/**
 * 检查 `@Mock` 注解的 delayRange 参数
 */
internal fun KSAnnotation.checkWithMockProviderDelayRange(
	delayRange: Array<Long>,
	funName: String,
) {
	compileErrorCheck(delayRange.size == 1 || (delayRange.size == 2 && delayRange[0] <= delayRange[1])) {
		COMPILE_MESSAGE_MOCK_PROVIDER_DELAY_RANGE.format(funName)
	}
}

/**
 * 检查错误，如果错误给用户提供错误点以及错误信息
 */
private inline fun KSNode.compileErrorCheck(
	value: Boolean,
	lazyMessage: () -> String,
) {
	if (!value) {
		kspLogger?.error(COMPILE_MESSAGE_KTORFITX.format(lazyMessage()), this)
	}
}