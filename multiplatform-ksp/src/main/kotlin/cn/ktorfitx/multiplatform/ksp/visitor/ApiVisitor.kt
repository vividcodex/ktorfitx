package cn.ktorfitx.multiplatform.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.check.ktorfitxError
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.*
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility.INTERNAL
import com.google.devtools.ksp.symbol.Visibility.PUBLIC
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object ApiVisitor : KSEmptyVisitor<Unit, ClassModel>() {
	
	private val apiUrlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
	
	override fun visitClassDeclaration(
		classDeclaration: KSClassDeclaration,
		data: Unit
	): ClassModel = classDeclaration.getClassModel()
	
	private fun KSClassDeclaration.getClassModel(): ClassModel {
		val className = ClassName("${packageName.asString()}.impls", "${simpleName.asString()}Impl")
		val superinterface = this.toClassName()
		return ClassModel(
			className = className,
			superinterface = superinterface,
			kModifier = this.getVisibilityKModifier(),
			apiUrl = this.getApiUrl(),
			apiScopeModels = this.getApiScopeModels(),
			funModels = getFunModel()
		)
	}
	
	private fun KSClassDeclaration.getApiUrl(): String? {
		val annotation = getKSAnnotationByType(TypeNames.Api)!!
		val url = annotation.getValueOrNull<String>("url")?.trim('/') ?: return null
		if (url.isBlank()) return null
		annotation.compileCheck(!url.isHttpOrHttps() && !url.isWSOrWSS()) {
			"${simpleName.asString()} 接口上的 @Api 注解的 url 参数不允许开头是 http:// 或 https:// 或 ws:// 或 wss://"
		}
		annotation.compileCheck(apiUrlRegex.matches(url)) {
			"${simpleName.asString()} 接口上的 @Api 注解的 url 参数格式错误"
		}
		return url
	}
	
	private fun KSClassDeclaration.getApiScopeModels(): List<ApiScopeModel> {
		val apiScopeAnnotation = getKSAnnotationByType(TypeNames.ApiScope)
		val apiScopesAnnotation = getKSAnnotationByType(TypeNames.ApiScopes)
		val apiScopeClassNames = when {
			apiScopeAnnotation != null && apiScopesAnnotation != null -> {
				this.ktorfitxError {
					"${simpleName.asString()} 接口上不允许同时使用 @ApiScope 和 @ApiScopes 注解"
				}
			}
			
			apiScopeAnnotation != null -> {
				listOf(apiScopeAnnotation.getClassName("apiScope"))
			}
			
			apiScopesAnnotation != null -> {
				apiScopesAnnotation.getClassNamesOrNull("apiScopes")?.distinct() ?: this.ktorfitxError {
					"${simpleName.asString()} 接口上的 @ApiScopes 注解参数不允许为空"
				}
			}
			
			else -> listOf(TypeNames.DefaultApiScope)
		}
		val groupSize = apiScopeClassNames.groupBy { it.simpleNames.joinToString(".") }.size
		this.compileCheck(apiScopeClassNames.size == groupSize) {
			"${simpleName.asString()} 函数不允许使用相同的类名"
		}
		return apiScopeClassNames.map { ApiScopeModel(it) }
	}
	
	/**
	 * 获取访问权限的 KModifier
	 */
	private fun KSClassDeclaration.getVisibilityKModifier(): KModifier {
		val visibility = this.getVisibility()
		this.compileCheck(visibility == PUBLIC || visibility == INTERNAL) {
			"${simpleName.asString()} 接口标记了 @Api，所以必须是 public 或 internal 访问权限的"
		}
		return KModifier.entries.first { it.name == visibility.name }
	}
	
	private fun KSClassDeclaration.getFunModel(): List<FunModel> {
		return this.getDeclaredFunctions().toList()
			.filter { it.isAbstract }
			.map {
				it.compileCheck(Modifier.SUSPEND in it.modifiers) {
					"${simpleName.asString()} 函数缺少 suspend 修饰符"
				}
				val routeModel = it.getRouteModel()
				val isWebSocket = routeModel is WebSocketModel
				FunModel(
					funName = it.simpleName.asString(),
					returnModel = it.getReturnModel(isWebSocket),
					parameterModels = it.getParameterModels(isWebSocket),
					routeModel = routeModel,
					mockModel = it.getMockModel(isWebSocket),
					hasBearerAuth = it.hasBearerAuth(),
					timeoutModel = it.getTimeoutModel(),
					queryModels = it.getQueryModels(),
					pathModels = it.getPathModels(routeModel.url),
					cookieModels = it.getCookieModels(),
					attributeModels = it.getAttributeModels(),
					headerModels = it.getHeaderModels(),
					headersModel = it.getHeadersModel(),
					requestBodyModel = it.getRequestBodyModel()
				)
			}
	}
	
	private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
	
	private fun KSFunctionDeclaration.getRouteModel(): RouteModel {
		val classNames = TypeNames.routes.filter {
			hasAnnotation(it)
		}
		this.compileCheck(classNames.size <= 1) {
			val useAnnotations = classNames.joinToString { "@${it.simpleName}" }
			val useSize = classNames.size
			"${simpleName.asString()} 函数只允许使用一种类型注解，而你使用了 $useAnnotations $useSize 个"
		}
		this.compileCheck(classNames.size == 1) {
			val routes = TypeNames.routes.joinToString { "@${it.simpleName}" }
			"${simpleName.asString()} 函数缺少注解，请使用以下注解标记：$routes"
		}
		val className = classNames.first()
		val isWebSocket = className == TypeNames.WebSocket
		val url = getKSAnnotationByType(className)!!.getValue<String>("url").removePrefix("/").removeSuffix("/")
		if (isWebSocket) {
			this.compileCheck(!url.isHttpOrHttps()) {
				"${simpleName.asString()} 函数上的 @${className.simpleName} 注解不允许使用 http:// 或 https:// 协议"
			}
		} else {
			this.compileCheck(!url.isWSOrWSS()) {
				"${simpleName.asString()} 函数上的 @${className.simpleName} 注解不允许使用 ws:// 或 wss:// 协议"
			}
		}
		this.compileCheck(urlRegex.matches(url)) {
			"${simpleName.asString()} 函数上的 @${className.simpleName} 注解的 url 参数格式错误"
		}
		return if (className == TypeNames.WebSocket) {
			WebSocketModel(url)
		} else {
			HttpRequestModel(url, className)
		}
	}
	
	private fun KSFunctionDeclaration.getReturnModel(isWebSocket: Boolean): ReturnModel {
		val returnType = this.returnType!!
		val typeName = returnType.toTypeName()
		val returnKind = when {
			isWebSocket -> {
				returnType.compileCheck(!typeName.isNullable && typeName == TypeNames.Unit) {
					"${simpleName.asString()} 函数必须使用 ${TypeNames.Unit.canonicalName} 作为返回类型，因为你已经标记了 @WebSocket 注解"
				}
				ReturnKind.Unit
			}
			
			typeName.rawType == TypeNames.Result -> {
				returnType.compileCheck(!typeName.isNullable && typeName is ParameterizedTypeName) {
					"${simpleName.asString()} 函数不允许为 Result 返回类型设置为可空"
				}
				ReturnKind.Result
			}
			
			typeName == TypeNames.Unit -> {
				returnType.compileCheck(!typeName.isNullable) {
					"${simpleName.asString()} 函数不允许使用 Unit? 返回类型"
				}
				ReturnKind.Unit
			}
			
			else -> {
				returnType.compileCheck(typeName != TypeNames.Nothing) {
					"${simpleName.asString()} 函数不允许使用 Nothing 返回类型"
				}
				ReturnKind.Any
			}
		}
		return ReturnModel(typeName, returnKind)
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): ClassModel = error("Not Implemented")
}