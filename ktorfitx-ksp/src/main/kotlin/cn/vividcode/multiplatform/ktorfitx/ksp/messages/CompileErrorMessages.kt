package cn.vividcode.multiplatform.ktorfitx.ksp.messages

import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 错误信息配置
 */
private const val BODY_SIZE_MESSAGE = "%s 方法的参数列表中不允许使用多个 @Body 注解"

/**
 * `@Body` 数量错误提示
 */
@OptIn(ExperimentalContracts::class)
internal fun KSFunctionDeclaration.checkWithBodySize(
	value: Boolean,
) {
	contract { returns() implies value }
	check(value) {
		BODY_SIZE_MESSAGE.format(qualifiedName!!.asString())
	}
}

private const val BODY_TYPE_MESSAGE = "%s 方法的参数列表中标记了 @Body 注解，但是未找到参数类型"

/**
 * `@Body` 类型错误提示
 */
@OptIn(ExperimentalContracts::class)
internal fun KSFunctionDeclaration.checkWithBodyType(
	value: Boolean,
) {
	contract { returns() implies value }
	check(value) {
		BODY_TYPE_MESSAGE.format(qualifiedName!!.asString())
	}
}

private const val NOT_FOUND_REQUEST_METHOD_MESSAGE = "%s 方法缺少请求类型，请使用以下请求类型类型："

/**
 * 没有找到请求类型错误提示
 */
@OptIn(ExperimentalContracts::class)
internal fun KSFunctionDeclaration.checkWithNotFoundRequestMethod(
	value: Boolean,
) {
	contract { returns() implies value }
	check(value) {
		val requestMethods = RequestMethod.entries.joinToString { "@${it.annotation.simpleName!!}" }
		NOT_FOUND_REQUEST_METHOD_MESSAGE.format(qualifiedName!!.asString(), requestMethods)
	}
}

private const val MULTIPLE_REQUEST_METHOD = "%s 方法只允许使用一种请求方法，而你使用了 %s %d 个"

/**
 *
 */
@OptIn(ExperimentalContracts::class)
internal fun KSFunctionDeclaration.checkWithMultipleRequestMethod(
	value: Boolean,
	annotations: List<KSAnnotation>,
) {
	contract { returns() implies value }
	check(value) {
		MULTIPLE_REQUEST_METHOD.format(
			qualifiedName!!.asString(),
			annotations.joinToString { "@${it.shortName.asString()}" },
			annotations.size
		)
	}
}

private inline fun check(value: Boolean, lazyMessage: () -> String) {
	if (!value) {
		error(lazyMessage())
	}
}