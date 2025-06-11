package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isWSOrWSS
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ApiModel
import cn.vividcode.multiplatform.ktorfitx.websockets.WebSocket
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
	
	private val requestMethodAnnotations by lazy {
		RequestMethod.entries.map { it.annotation }
	}
	
	fun KSFunctionDeclaration.resolve(isWebSocket: Boolean): ApiModel {
		val funName = this.simpleName.asString()
		return if (isWebSocket) {
			val annotations = requestMethodAnnotations.mapNotNull {
				getKSAnnotationByType(it)
			}
			this.compileCheck(annotations.isEmpty()) {
				val requestMethods = requestMethodAnnotations.joinToString { "@${it.simpleName!!}" }
				"$funName 方法不允许使用 $requestMethods 这些注解，因为你已经标记了 @WebSocket 注解"
			}
			val annotation = getKSAnnotationByType(WebSocket::class)!!
			ApiModel("", annotation.getUrl(funName, true))
		} else {
			val annotations = requestMethodAnnotations.mapNotNull {
				getKSAnnotationByType(it)
			}
			this.compileCheck(annotations.isNotEmpty()) {
				val requestMethods = requestMethodAnnotations.joinToString { "@${it.simpleName!!}" }
				"$funName 方法缺少注解，请使用以下注解标记：$requestMethods"
			}
			this.compileCheck(annotations.size == 1) {
				val useAnnotations = annotations.joinToString()
				val useSize = annotations.size
				"$funName 方法只允许使用一种类型注解，而你使用了 $useAnnotations $useSize 个"
			}
			val annotation = annotations.first()
			val requestFunName = annotation.shortName.asString().lowercase()
			ApiModel(requestFunName, annotation.getUrl(funName, false))
		}
	}
	
	private fun KSAnnotation.getUrl(funName: String, isWebSocket: Boolean): String {
		val url = this.getValue<String>("url")!!
		if (isWebSocket) {
			if (url.isWSOrWSS()) return url
			this.compileCheck(!url.isHttpOrHttps()) {
				"$funName 方法上的 $this 注解不允许使用 http:// 或 https:// 协议"
			}
		} else {
			if (url.isHttpOrHttps()) return url
			this.compileCheck(!url.isWSOrWSS()) {
				"$funName 方法上的 $this 注解不允许使用 ws:// 或 wss:// 协议"
			}
		}
		this.compileCheck(urlRegex.matches(url)) {
			"$funName 方法上的 $this 注解的 url 参数格式错误"
		}
		return url
	}
}