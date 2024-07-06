package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.api.annotation.*
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.RequestType
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ApiModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.ktor.util.reflect.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午2:17
 *
 * 介绍：ApiModelResolver
 */
@Suppress("unused")
internal data object ApiModelResolver : FunctionModelResolver<ApiModel> {
	
	private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
	
	override fun KSFunctionDeclaration.resolve(): ApiModel {
		val annotation = RequestType.entries.mapNotNull {
			getAnnotationByType(it.annotation)
		}.also { annotations ->
			check(annotations.size <= 1) {
				val requestTypes = annotations.joinToString { "@${it::class.simpleName}" }
				"${qualifiedName!!.asString()} 方法只允许使用一种请求方法，而你使用了 $requestTypes ${annotations.size} 种"
			}
			check(annotations.isNotEmpty()) {
				val requestTypes = RequestType.entries.joinToString { "@${it::class.simpleName}" }
				"${qualifiedName!!.asString()} 至少在 $requestTypes 中使用一种请求方式"
			}
		}.first()
		return RequestType.entries.first { annotation.instanceOf(it.annotation) }.let {
			val funName = this.simpleName.asString()
			when (annotation) {
				is GET -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				is POST -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				is PUT -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				is DELETE -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				is PATCH -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				is OPTIONS -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				is HEAD -> ApiModel(it, format(annotation.url, funName), annotation.auth)
				else -> error("不支持的请求类型")
			}
		}
	}
	
	private fun format(url: String, funName: String): String {
		check(urlRegex.matches(url)) {
			"$funName 的 url 参数格式错误"
		}
		return if (url.startsWith('/')) url else "/$url"
	}
}