package cn.ktorfitx.server.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.model.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class RouteVisitor : KSEmptyVisitor<Unit, FunModel>() {
	
	override fun visitFunctionDeclaration(
		function: KSFunctionDeclaration,
		data: Unit
	): FunModel {
		val routeModel = function.getRouteModel()
		function.checkReturnType(routeModel !is HttpRequestModel)
		return with(ParameterResolver) {
			FunModel(
				funName = function.simpleName.asString(),
				canonicalName = function.getCanonicalName(),
				isExtension = function.extensionReceiver != null,
				group = function.getGroupName(),
				authenticationModel = function.getAuthenticationModel(),
				routeModel = routeModel,
				varNames = function.getVarNames(),
				principalModels = function.getPrincipalModels(),
				queryModels = function.getQueryModels(),
				pathModels = function.getPathModels(routeModel.path),
				headerModels = function.getHeaderModels(),
				cookieModels = function.getCookieModels(),
				requestBody = function.getRequestBody(routeModel),
			)
		}
	}
	
	private fun KSFunctionDeclaration.getCanonicalName(): String {
		val parent = this.parentDeclaration
		return when (parent) {
			is KSClassDeclaration -> parent.toClassName().canonicalName
			else -> this.packageName.asString()
		}
	}
	
	private fun KSFunctionDeclaration.getGroupName(): String? {
		val annotation = this.getKSAnnotationByType(ClassNames.Group) ?: return null
		return annotation.getValue("name")
	}
	
	private fun KSFunctionDeclaration.checkReturnType(
		isWebSocket: Boolean
	) {
		val returnType = this.returnType!!.resolve()
		
		this.compileCheck(!returnType.isMarkedNullable) {
			"${simpleName.asString()} 函数返回类型不允许为可空类型"
		}
		val typeName = returnType.toTypeName()
		if (isWebSocket) {
			this.compileCheck(typeName == ClassNames.Unit) {
				"${simpleName.asString()} 函数是 WebSocket 类型，返回类型必须是 Unit"
			}
		} else {
			val validType = typeName is ClassName || typeName is ParameterizedTypeName
			this.compileCheck(validType) {
				"${simpleName.asString()} 函数返回类型必须是明确的类"
			}
			this.compileCheck(typeName != ClassNames.Unit && typeName != ClassNames.Nothing) {
				"${simpleName.asString()} 函数不允许使用 Unit 和 Nothing 返回类型"
			}
		}
	}
	
	private fun KSFunctionDeclaration.getAuthenticationModel(): AuthenticationModel? {
		val annotation = this.getKSAnnotationByType(ClassNames.Authentication) ?: return null
		val configurations = annotation.getValues<String>("configurations")
		val strategy = annotation.getClassName("strategy")!!
		return AuthenticationModel(configurations, strategy)
	}
	
	private fun KSFunctionDeclaration.getRouteModel(): RouteModel {
		val dataList = ClassNames.routes.mapNotNull {
			this.getKSAnnotationByType(it)?.let(it::to)
		}
		this.compileCheck(dataList.size == 1) {
			"${simpleName.asString()} 函数不允许同时添加多个请求类型"
		}
		val data = dataList.first()
		val className = data.first
		val annotation = data.second
		val path = annotation.getValue<String>("path").removePrefix("/").removeSuffix("/")
		val isExtension = this.extensionReceiver != null
		return when (className) {
			ClassNames.WebSocket -> {
				val protocol = annotation.getValueOrNull("protocol") ?: ""
				if (isExtension) {
					val valid = this.isExtension(ClassNames.DefaultWebSocketServerSession)
					this.compileCheck(valid) {
						"${simpleName.asString()} 是扩展函数，但仅允许扩展 DefaultWebSocketServerSession"
					}
				}
				WebSocketModel(path, protocol)
			}
			
			ClassNames.WebSocketRaw -> {
				val protocol = annotation.getValueOrNull("protocol") ?: ""
				val negotiateExtensions = annotation.getValueOrNull("negotiateExtensions") ?: false
				if (isExtension) {
					val valid = this.isExtension(ClassNames.WebSocketServerSession)
					this.compileCheck(valid) {
						"${simpleName.asString()} 是扩展函数，但仅允许扩展 WebSocketServerSession"
					}
				}
				WebSocketRawModel(path, protocol, negotiateExtensions)
			}
			
			else -> {
				if (isExtension) {
					val valid = this.isExtension(ClassNames.RoutingContext)
					this.compileCheck(valid) {
						"${simpleName.asString()} 是扩展函数，但仅允许扩展 RoutingContext"
					}
				}
				HttpRequestModel(path, className)
			}
		}
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): FunModel = error("Not Implemented")
}