package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ApiModel
import com.google.devtools.ksp.symbol.KSAnnotation
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
		val funName = this.simpleName.asString()
		this.compileCheck(annotations.isNotEmpty()) {
			val requestMethods = RequestMethod.entries.joinToString { "@${it.annotation.simpleName!!}" }
			"$funName 方法缺少请求类型，请使用以下请求类型类型：$requestMethods"
		}
		this.compileCheck(annotations.size == 1) {
			val useAnnotations = annotations.joinToString()
			val useSize = annotations.size
			"$funName 方法只允许使用一种请求方法，而你使用了 $useAnnotations $useSize 个"
		}
		val annotation = annotations.first()
		val requestFunName = annotation.shortName.asString().lowercase()
		return ApiModel(requestFunName, annotation.getUrl(funName))
	}
	
	private fun KSAnnotation.getUrl(funName: String): String {
		val url = this.getValue<String>("url")!!
		if (url.isHttpOrHttps()) return url
		this.compileCheck(urlRegex.matches(url)) {
			"$funName 方法上 $this 注解的 url 参数格式错误"
		}
		return if (url.startsWith('/')) url else "/$url"
	}
}