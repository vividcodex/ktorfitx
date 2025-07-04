package cn.ktorfitx.server.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.model.AuthenticationModel
import cn.ktorfitx.server.ksp.model.RouteModel
import cn.ktorfitx.server.ksp.model.toRequestMethod
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class RouteVisitor : KSEmptyVisitor<Unit, RouteModel?>() {
	
	override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit): RouteModel? {
		val isRoutingContextExtension = function.isExtension(ClassNames.RoutingContext)
		function.compileCheck(isRoutingContextExtension) {
			"${function.simpleName} 方法必须是 RoutingContext 的扩展类"
		}
		val routeModel = function.getRouteModel()
		val authentication = function.getKSAnnotationByType(ClassNames.Authentication)
		if (authentication == null) {
			return routeModel
		}
		return getAuthenticationModel(authentication, routeModel)
	}
	
	private fun KSFunctionDeclaration.getRouteModel(): RouteModel {
		val dataList = ClassNames.requestMethodClassNames.mapNotNull { className ->
			this.getKSAnnotationByType(className)?.let {
				className to it
			}
		}
		this.compileCheck(dataList.size == 1) {
			"${this.simpleName} 不允许同时添加多个请求类型"
		}
		val data = dataList.first()
		val requestMethod = data.first.toRequestMethod()
		val path = data.second.getValue<String>("path")!!
			.trim().removePrefix("/").removeSuffix("/")
		val parent = this.parentDeclaration
		val functionClassName = when (parent) {
			is KSClassDeclaration -> {
				val parent = parent.toClassName()
				ClassName(parent.canonicalName, this.simpleName.asString())
			}
			
			else -> {
				val packageName = this.packageName.asString()
				val simpleName = this.simpleName.asString()
				ClassName(packageName, simpleName)
			}
		}
		val returnType = this.returnType!!.resolve()
		returnType.declaration.compileCheck(!returnType.isMarkedNullable) {
			"${this.simpleName} 方法返回类型不允许为可空类型"
		}
		val typeName = returnType.toTypeName()
		val validTypeName = typeName is ClassName || typeName is ParameterizedTypeName
		returnType.declaration.compileCheck(validTypeName) {
			"${this.simpleName} 方法返回类型必须是明确的类"
		}
		return RouteModel(functionClassName, requestMethod, path, typeName)
	}
	
	private fun getAuthenticationModel(
		authentication: KSAnnotation,
		routeModel: RouteModel
	): AuthenticationModel {
		val configurations = authentication.getValues<String>("configurations")!!
		val strategy = authentication.getClassName("strategy")!!
		return AuthenticationModel(
			functionClassName = routeModel.functionClassName,
			requestMethod = routeModel.requestMethod,
			path = routeModel.path,
			returnTypeName = routeModel.returnTypeName,
			configurations = configurations,
			strategy = strategy
		)
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): RouteModel? = null
}