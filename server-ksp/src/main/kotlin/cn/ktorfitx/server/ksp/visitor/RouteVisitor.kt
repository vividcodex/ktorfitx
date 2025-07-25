package cn.ktorfitx.server.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.server.ksp.constants.TypeNames
import cn.ktorfitx.server.ksp.model.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class RouteVisitor : KSEmptyVisitor<List<CustomHttpMethodModel>, FunModel>() {
	
	override fun visitFunctionDeclaration(
		function: KSFunctionDeclaration,
		data: List<CustomHttpMethodModel>
	): FunModel {
		function.compileCheck(!(function.isGeneric())) {
			"${function.simpleName.asString()} 函数不允许包含泛型"
		}
		val routeModel = function.getRouteModel(data)
		function.checkReturnType(routeModel is HttpRequestModel)
		return FunModel(
			funName = function.simpleName.asString(),
			canonicalName = function.getCanonicalName(),
			isExtension = function.extensionReceiver != null,
			group = function.getGroupName(),
			authenticationModel = function.getAuthenticationModel(),
			routeModel = routeModel,
			regexModel = function.getRegexModel(routeModel),
			varNames = function.getVarNames(),
			principalModels = function.getPrincipalModels(),
			queryModels = function.getQueryModels(),
			pathModels = function.getPathModels(routeModel),
			headerModels = function.getHeaderModels(),
			cookieModels = function.getCookieModels(),
			attributeModels = function.getAttributeModels(),
			requestBodyModel = function.getRequestBody(routeModel),
		)
	}
	
	private fun KSFunctionDeclaration.getCanonicalName(): String {
		val parent = this.parentDeclaration
		return when (parent) {
			is KSClassDeclaration -> parent.toClassName().canonicalName
			else -> this.packageName.asString()
		}
	}
	
	private fun KSFunctionDeclaration.getGroupName(): String? {
		val annotation = this.getKSAnnotationByType(TypeNames.Group) ?: return null
		return annotation.getValue("name")
	}
	
	private fun KSFunctionDeclaration.checkReturnType(
		isHttpRequest: Boolean
	) {
		val returnType = this.returnType!!.resolve()
		
		this.compileCheck(!returnType.isMarkedNullable) {
			"${simpleName.asString()} 函数返回类型不允许为可空类型"
		}
		val typeName = returnType.toTypeName()
		if (isHttpRequest) {
			val validType = typeName is ClassName || typeName is ParameterizedTypeName
			this.compileCheck(validType) {
				"${simpleName.asString()} 函数返回类型必须是明确的类"
			}
			this.compileCheck(typeName != TypeNames.Unit && typeName != TypeNames.Nothing) {
				"${simpleName.asString()} 函数不允许使用 Unit 和 Nothing 返回类型"
			}
		} else {
			this.compileCheck(typeName == TypeNames.Unit) {
				"${simpleName.asString()} 函数是 WebSocket 类型，返回类型必须是 Unit"
			}
		}
	}
	
	private fun KSFunctionDeclaration.getAuthenticationModel(): AuthenticationModel? {
		val annotation = this.getKSAnnotationByType(TypeNames.Authentication) ?: return null
		val configurations = annotation.getValues<String>("configurations")
		val strategy = annotation.getClassNameOrNull("strategy") ?: TypeNames.AuthenticationStrategyFirstSuccessful
		return AuthenticationModel(configurations, strategy)
	}
	
	private fun KSFunctionDeclaration.getRouteModel(
		customHttpMethodModels: List<CustomHttpMethodModel>
	): RouteModel {
		val customHttpMethodClassNames = customHttpMethodModels.map { it.className }
		val dataList = (TypeNames.routes + customHttpMethodClassNames)
			.mapNotNull { this.getKSAnnotationByType(it)?.let(it::to) }
		this.compileCheck(dataList.size == 1) {
			"${simpleName.asString()} 函数不允许同时添加多个请求类型"
		}
		val data = dataList.first()
		val className = data.first
		val annotation = data.second
		val path = annotation.getValue<String>("path").removePrefix("/").removeSuffix("/")
		val isExtension = this.extensionReceiver != null
		return when (className) {
			TypeNames.WebSocket -> {
				val protocol = annotation.getValueOrNull<String>("protocol")?.takeIf { it.isNotBlank() }
				if (isExtension) {
					val valid = this.isExtension(TypeNames.DefaultWebSocketServerSession)
					this.compileCheck(valid) {
						"${simpleName.asString()} 是扩展函数，但仅允许扩展 DefaultWebSocketServerSession"
					}
				}
				WebSocketModel(path, protocol, annotation)
			}
			
			TypeNames.WebSocketRaw -> {
				val protocol = annotation.getValueOrNull<String>("protocol")?.takeIf { it.isNotBlank() }
				val negotiateExtensions = annotation.getValueOrNull("negotiateExtensions") ?: false
				if (isExtension) {
					val valid = this.isExtension(TypeNames.WebSocketServerSession)
					this.compileCheck(valid) {
						"${simpleName.asString()} 是扩展函数，但仅允许扩展 WebSocketServerSession"
					}
				}
				WebSocketRawModel(path, protocol, negotiateExtensions, annotation)
			}
			
			else -> {
				if (isExtension) {
					val valid = this.isExtension(TypeNames.RoutingContext)
					this.compileCheck(valid) {
						"${simpleName.asString()} 是扩展函数，但仅允许扩展 RoutingContext"
					}
				}
				if (className in TypeNames.httpMethods) {
					HttpRequestModel(path, annotation, className.simpleName, false)
				} else {
					val method = customHttpMethodModels.first { it.className == className }.method
					HttpRequestModel(path, annotation, method, true)
				}
			}
		}
	}
	
	private fun KSFunctionDeclaration.getRegexModel(
		routeModel: RouteModel
	): RegexModel? {
		val annotation = this.getKSAnnotationByType(TypeNames.Regex) ?: return null
		val classNames = annotation.getClassNamesOrNull("options")?.toSet() ?: emptySet()
		val options = classNames.map { RegexOption.valueOf(it.simpleName) }.toSet()
		routeModel.annotation.compileCheck(routeModel.path.isValidRegex(options)) {
			"${simpleName.asString()} 函数上的 @${routeModel.annotation.shortName.asString()} 注解的 path 参数不是一个合法的正则表达式"
		}
		return RegexModel(classNames)
	}
	
	override fun defaultHandler(node: KSNode, data: List<CustomHttpMethodModel>): FunModel = error("Not Implemented")
}