package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.annotation.*
import cn.vividcode.multiplatform.ktorfit.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktorfit.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.ApiModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 14:17
 *
 * 文件介绍：ApiModelResolver
 */
@Suppress("unused")
internal data object ApiModelResolver : FunctionModelResolver<ApiModel> {
	
	private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
	
	override fun KSFunctionDeclaration.resolve(): ApiModel {
		val annotation = RequestMethod.entries.mapNotNull {
			getAnnotationByType(it.annotation)
		}.also { annotations ->
			check(annotations.size <= 1) {
				val requestMethods = annotations.joinToString { "@${it::class.simpleName}" }
				"${qualifiedName!!.asString()} 方法只允许使用一种请求方法，而你使用了 $requestMethods ${annotations.size} 种"
			}
			check(annotations.isNotEmpty()) {
				val requestMethods = RequestMethod.entries.joinToString { "@${it::class.simpleName}" }
				"${qualifiedName!!.asString()} 至少在 $requestMethods 中使用一种请求方式"
			}
		}.first()
		val funName = this.simpleName.asString()
		return when (annotation) {
			is GET -> ApiModel("get", format(annotation.url, funName), annotation.auth)
			is POST -> ApiModel("post", format(annotation.url, funName), annotation.auth)
			is PUT -> ApiModel("put", format(annotation.url, funName), annotation.auth)
			is DELETE -> ApiModel("delete", format(annotation.url, funName), annotation.auth)
			is HEAD -> ApiModel("head", format(annotation.url, funName), annotation.auth)
			is PATCH -> ApiModel("patch", format(annotation.url, funName), annotation.auth)
			is OPTIONS -> ApiModel("options", format(annotation.url, funName), annotation.auth)
			else -> error("不支持的请求类型")
		}
	}
	
	private fun format(url: String, funName: String): String {
		check(urlRegex.matches(url)) {
			"$funName 的 url 参数格式错误"
		}
		return if (url.startsWith('/')) url else "/$url"
	}
}