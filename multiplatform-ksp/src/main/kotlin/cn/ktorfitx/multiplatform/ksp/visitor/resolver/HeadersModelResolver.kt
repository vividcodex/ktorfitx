package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.multiplatform.annotation.Headers
import cn.ktorfitx.multiplatform.ksp.expends.getKSAnnotationByType
import cn.ktorfitx.multiplatform.ksp.expends.getValues
import cn.ktorfitx.multiplatform.ksp.model.model.HeadersModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/2 22:22
 *
 * 文件介绍：HeadersModelResolver
 */
internal object HeadersModelResolver {
	
	private val headersRegex = "^([^:=]+)[:=]([^:=]+)$".toRegex()
	
	fun KSFunctionDeclaration.resolve(): HeadersModel? {
		val annotation  = getKSAnnotationByType(Headers::class) ?: return null
		val headers = annotation.getValues(Headers::headers) ?: return null
		return headers.associate {
			val (name, value) = headersRegex.matchEntire(it)?.destructured
				?: error("${qualifiedName!!.asString()} 方法的 @Headers 格式错误")
			name.trim() to value.trim()
		}.let { HeadersModel(it) }
	}
}