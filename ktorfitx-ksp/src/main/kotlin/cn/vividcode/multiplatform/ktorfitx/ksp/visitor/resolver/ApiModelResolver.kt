package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ApiModel
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
internal object ApiModelResolver {
	
	private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
	
	fun KSFunctionDeclaration.resolve(): ApiModel {
		val annotation = RequestMethod.entries.mapNotNull {
			getKSAnnotationByType(it.annotation)
		}.also { annotations ->
			check(annotations.size <= 1) {
				val requestMethods = annotations.joinToString {
					"@${it.shortName.asString()}"
				}
				"${qualifiedName!!.asString()} 方法只允许使用一种请求方法，而你使用了 $requestMethods ${annotations.size} 种"
			}
			check(annotations.isNotEmpty()) {
				val requestMethods = RequestMethod.entries.joinToString { "@${it.annotation.simpleName!!}" }
				"缺少请求类型，${qualifiedName!!.asString()} 方法需要使用 $requestMethods 中的一种"
			}
		}.first()
		val funName = this.simpleName.asString()
		val requestFunName = annotation.shortName.asString().lowercase()
		val url = annotation.getValue<String>("url")!!
		return ApiModel(requestFunName, formatUrl(url, funName))
	}
	
	private fun formatUrl(url: String, funName: String): String {
		if (url.isHttpOrHttps()) return url
		check(urlRegex.matches(url)) {
			"$funName 的 url 参数格式数据"
		}
		return if (url.startsWith('/')) url else "/$url"
	}
}