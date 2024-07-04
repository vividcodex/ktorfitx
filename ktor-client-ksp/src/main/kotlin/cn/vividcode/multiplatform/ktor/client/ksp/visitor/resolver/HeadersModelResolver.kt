package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.api.annotation.Headers
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.HeadersModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/2 下午10:22
 *
 * 介绍：HeadersModelResolver
 */
internal data object HeadersModelResolver : FunctionModelResolver<HeadersModel> {
	
	private val headersRegex = "^([^:=]+)[:=]([^:=]+)$".toRegex()
	
	override fun KSFunctionDeclaration.getFunctionModel(): HeadersModel? {
		val headers = getAnnotationByType(Headers::class) ?: return null
		val headerMap = mutableMapOf<String, String>()
		headers.values.associate {
			val (name, value) = headersRegex.matchEntire(it)?.destructured
				?: error("${this.simpleName.asString()} 方法的 @Headers 格式错误")
			name to value
		}
		return if (headerMap.isNotEmpty()) HeadersModel(headerMap) else null
	}
}