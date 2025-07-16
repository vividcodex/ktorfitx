package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValuesOrNull
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.HeadersModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

private val headersRegex = "^([^:=]+)[:=]([^:=]+)$".toRegex()

internal fun KSFunctionDeclaration.resolveHeadersModel(): HeadersModel? {
	val annotation = getKSAnnotationByType(ClassNames.Headers) ?: return null
	val headers = annotation.getValuesOrNull<String>("headers") ?: return null
	return headers.associate {
		val (name, value) = headersRegex.matchEntire(it)?.destructured
			?: error("${qualifiedName!!.asString()} 函数的 @Headers 格式错误")
		name.trim() to value.trim()
	}.let { HeadersModel(it) }
}