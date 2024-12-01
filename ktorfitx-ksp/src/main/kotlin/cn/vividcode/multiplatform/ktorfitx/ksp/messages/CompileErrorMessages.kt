@file:OptIn(ExperimentalContracts::class)

package cn.vividcode.multiplatform.ktorfitx.ksp.messages

import cn.vividcode.multiplatform.ktorfitx.ksp.kspLogger
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private const val BODY_SIZE_MESSAGE = "%s 方法的参数列表中不允许使用多个 @Body 注解"

/**
 * `@Body` 数量错误提示
 */
internal fun KSFunctionDeclaration.checkWithBodySize(
	value: Boolean,
) {
	check(value) {
		BODY_SIZE_MESSAGE.format(qualifiedName!!.asString())
	}
}

private const val BODY_TYPE_MESSAGE = "%s 方法的参数列表中标记了 @Body 注解，但是未找到参数类型"

/**
 * `@Body` 类型错误提示
 */
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
internal fun KSFunctionDeclaration.checkWithNotFoundRequestMethod(
	value: Boolean,
) {
	check(value) {
		val requestMethods = RequestMethod.entries.joinToString { "@${it.annotation.simpleName!!}" }
		NOT_FOUND_REQUEST_METHOD_MESSAGE.format(qualifiedName!!.asString(), requestMethods)
	}
}

private const val MULTIPLE_REQUEST_METHOD_MESSAGE = "%s 方法只允许使用一种请求方法，而你使用了 %s %d 个"

/**
 * 找到多个请求类型错误提示
 */
internal fun KSFunctionDeclaration.checkWithMultipleRequestMethod(
	value: Boolean,
	annotations: List<KSAnnotation>,
) {
	check(value) {
		MULTIPLE_REQUEST_METHOD_MESSAGE.format(
			qualifiedName!!.asString(),
			annotations.joinToString { "@${it.shortName.asString()}" },
			annotations.size
		)
	}
}

private const val URL_REGEX_MESSAGE = "%s 方法中注解上标记的 url 参数格式错误"

/**
 * 检查 url 的格式
 */
internal fun KSFunctionDeclaration.checkWithUrlRegex(
	value: Boolean,
) {
	check(value) {
		URL_REGEX_MESSAGE.format(this.qualifiedName!!.asString())
	}
}

private inline fun KSNode.check(value: Boolean, lazyMessage: () -> String) {
	if (!value) {
		kspLogger?.error(lazyMessage(), this)
	}
}

/**
 * Ktorfitx 编译异常
 */
class KtorfitxCompileException(message: String) : Exception(message)