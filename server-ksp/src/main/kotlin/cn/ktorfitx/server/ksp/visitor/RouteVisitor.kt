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
	
	override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit): FunModel {
		function.checkReturnType()
		return FunModel(
			function.simpleName.asString(),
			function.getCanonicalName(),
			function.extensionReceiver != null,
			function.getGroupName(),
			function.getAuthenticationModel(),
			function.getRouteModel(),
			function.getVarNames(),
			function.getPrincipalModels(),
			function.getBodyModel(),
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
		val annotation = this.getKSAnnotationByType(ClassNames.Group) ?: return null
		return annotation.getValue("name")
	}
	
	private fun KSFunctionDeclaration.checkReturnType() {
		val returnType = this.returnType!!.resolve()
		returnType.declaration.compileCheck(!returnType.isMarkedNullable) {
			"${this.simpleName} 函数返回类型不允许为可空类型"
		}
		val typeName = returnType.toTypeName()
		val validTypeName = typeName is ClassName || typeName is ParameterizedTypeName
		returnType.declaration.compileCheck(validTypeName) {
			"${this.simpleName} 函数返回类型必须是明确的类"
		}
	}
	
	private fun KSFunctionDeclaration.getAuthenticationModel(): AuthenticationModel? {
		val annotation = this.getKSAnnotationByType(ClassNames.Authentication) ?: return null
		val configurations = annotation.getValues<String>("configurations")!!
		val strategy = annotation.getClassName("strategy")!!
		return AuthenticationModel(configurations, strategy)
	}
	
	private fun KSFunctionDeclaration.getRouteModel(): RouteModel {
		val dataList = ClassNames.routes.mapNotNull {
			this.getKSAnnotationByType(it)?.let(it::to)
		}
		this.compileCheck(dataList.size == 1) {
			"${this.simpleName} 不允许同时添加多个请求类型"
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
						"${this.simpleName} 是扩展函数，但仅允许扩展 DefaultWebSocketServerSession"
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
						"${this.simpleName} 是扩展函数，但仅允许扩展 WebSocketServerSession"
					}
				}
				WebSocketRawModel(path, protocol, negotiateExtensions)
			}
			
			else -> {
				if (isExtension) {
					val valid = this.isExtension(ClassNames.RoutingContext)
					this.compileCheck(valid) {
						"${this.simpleName} 是扩展函数，但仅允许扩展 RoutingContext"
					}
				}
				val method = className.simpleName.lowercase()
				HttpRequestModel(path, method)
			}
		}
	}
	
	private fun KSFunctionDeclaration.getVarNames(): List<String> {
		return this.parameters.mapNotNull { parameter ->
			parameter.name?.asString()?.takeIf { it.isNotBlank() }
		}
	}
	
	private fun KSFunctionDeclaration.getPrincipalModels(): List<PrincipalModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Principal) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
			}
			val provider = annotation.getValueOrNull<String>("provider")?.takeIf { it.isNotBlank() }
			PrincipalModel(varName, typeName, isNullable, provider)
		}
	}
	
	private fun KSFunctionDeclaration.getBodyModel(): BodyModel? {
		val filters = this.parameters.filter { it.hasAnnotation(ClassNames.Body) }
		if (filters.isEmpty()) return null
		this.compileCheck(filters.size == 1) {
			"${simpleName.asString()} 函数参数中不允许使用多个 @Body"
		}
		val parameter = filters.single()
		val varName = parameter.name!!.asString()
		var typeName = parameter.type.toTypeName()
		val isNullable = typeName.isNullable
		if (isNullable) {
			typeName = typeName.copy(nullable = false)
		}
		return BodyModel(varName, typeName, isNullable)
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): FunModel = error("Not Implemented")
}