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

internal class RouteVisitor : KSEmptyVisitor<Unit, FunctionModel>() {
	
	override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit): FunctionModel {
		function.checkReturnType()
		return FunctionModel(
			function.simpleName.asString(),
			function.getCanonicalName(),
			function.getGroupName(),
			function.getAuthenticationModel(),
			function.getRouteModel(),
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
			"${this.simpleName} 方法返回类型不允许为可空类型"
		}
		val typeName = returnType.toTypeName()
		val validTypeName = typeName is ClassName || typeName is ParameterizedTypeName
		returnType.declaration.compileCheck(validTypeName) {
			"${this.simpleName} 方法返回类型必须是明确的类"
		}
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
		return when (className) {
			ClassNames.WebSocket -> {
				val protocol = annotation.getValueOrNull("protocol") ?: ""
				val valid = this.isExtension(ClassNames.DefaultWebSocketServerSession)
				this.compileCheck(valid) {
					"${this.simpleName} 方法必须是 DefaultWebSocketServerSession 的扩展方法"
				}
				WebSocketModel(path, protocol)
			}
			
			ClassNames.WebSocketRaw -> {
				val protocol = annotation.getValueOrNull("protocol") ?: ""
				val negotiateExtensions = annotation.getValueOrNull("negotiateExtensions") ?: false
				val valid = this.isExtension(ClassNames.WebSocketServerSession)
				this.compileCheck(valid) {
					"${this.simpleName} 方法必须是 WebSocketServerSession 的扩展方法"
				}
				WebSocketRawModel(path, protocol, negotiateExtensions)
			}
			
			else -> {
				val valid = this.isExtension(ClassNames.RoutingContext)
				this.compileCheck(valid) {
					"${this.simpleName} 方法必须是 RoutingContext 的扩展方法"
				}
				val method = className.simpleName.lowercase()
				HttpRequestModel(path, method)
			}
		}
	}
	
	private fun KSFunctionDeclaration.getAuthenticationModel(): AuthenticationModel? {
		val annotation = this.getKSAnnotationByType(ClassNames.Authentication) ?: return null
		val configurations = annotation.getValues<String>("configurations")!!
		val strategy = annotation.getClassName("strategy")!!
		return AuthenticationModel(configurations, strategy)
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): FunctionModel = error("Not Implemented")
}