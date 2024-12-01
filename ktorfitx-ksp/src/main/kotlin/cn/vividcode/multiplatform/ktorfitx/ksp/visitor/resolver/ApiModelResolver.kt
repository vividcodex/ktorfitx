package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.messages.checkWithMultipleRequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.messages.checkWithNotFoundRequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ApiModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
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
		val annotations = RequestMethod.entries.mapNotNull {
			getKSAnnotationByType(it.annotation)
		}
		this.checkWithNotFoundRequestMethod(annotations.isNotEmpty())
		this.checkWithMultipleRequestMethod(annotations.size == 1, annotations)
		val annotation = annotations.first()
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