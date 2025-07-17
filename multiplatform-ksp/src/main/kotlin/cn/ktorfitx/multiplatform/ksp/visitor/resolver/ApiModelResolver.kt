package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValue
import cn.ktorfitx.common.ksp.util.expends.isHttpOrHttps
import cn.ktorfitx.common.ksp.util.expends.isWSOrWSS
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.ApiModel
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()

private val requestMethodClassNames = arrayOf(
	TypeNames.GET,
	TypeNames.POST,
	TypeNames.PUT,
	TypeNames.DELETE,
	TypeNames.PATCH,
	TypeNames.HEAD,
	TypeNames.OPTIONS,
)

internal fun KSFunctionDeclaration.resolveApiModel(isWebSocket: Boolean): ApiModel {
	val funName = this.simpleName.asString()
	return if (isWebSocket) {
		val annotations = requestMethodClassNames.mapNotNull {
			getKSAnnotationByType(it)
		}
		this.compileCheck(annotations.isEmpty()) {
			val requestMethods = requestMethodClassNames.joinToString { "@${it.simpleName}" }
			"$funName 函数不允许使用 $requestMethods 这些注解，因为你已经标记了 @WebSocket 注解"
		}
		val annotation = getKSAnnotationByType(TypeNames.WebSocket)!!
		ApiModel("", annotation.getUrl(funName, true))
	} else {
		val annotations = requestMethodClassNames.mapNotNull {
			getKSAnnotationByType(it)
		}
		this.compileCheck(annotations.isNotEmpty()) {
			val requestMethods = requestMethodClassNames.joinToString { "@${it.simpleName}" }
			"$funName 函数缺少注解，请使用以下注解标记：$requestMethods"
		}
		this.compileCheck(annotations.size == 1) {
			val useAnnotations = annotations.joinToString()
			val useSize = annotations.size
			"$funName 函数只允许使用一种类型注解，而你使用了 $useAnnotations $useSize 个"
		}
		val annotation = annotations.first()
		val requestFunName = annotation.shortName.asString().lowercase()
		ApiModel(requestFunName, annotation.getUrl(funName, false))
	}
}

private fun KSAnnotation.getUrl(funName: String, isWebSocket: Boolean): String {
	val url = this.getValue<String>("url")
	if (isWebSocket) {
		if (url.isWSOrWSS()) return url
		this.compileCheck(!url.isHttpOrHttps()) {
			"$funName 函数上的 $this 注解不允许使用 http:// 或 https:// 协议"
		}
	} else {
		if (url.isHttpOrHttps()) return url
		this.compileCheck(!url.isWSOrWSS()) {
			"$funName 函数上的 $this 注解不允许使用 ws:// 或 wss:// 协议"
		}
	}
	this.compileCheck(urlRegex.matches(url)) {
		"$funName 函数上的 $this 注解的 url 参数格式错误"
	}
	return url.trim('/')
}