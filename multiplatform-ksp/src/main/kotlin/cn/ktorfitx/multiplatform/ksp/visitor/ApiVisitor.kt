package cn.ktorfitx.multiplatform.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.WebSocketModel
import cn.ktorfitx.multiplatform.ksp.model.structure.*
import cn.ktorfitx.multiplatform.ksp.visitor.resolver.ModelResolvers
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.Visibility.INTERNAL
import com.google.devtools.ksp.symbol.Visibility.PUBLIC
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object ApiVisitor : KSEmptyVisitor<Unit, ClassStructure>() {
	
	private val apiUrlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
	
	override fun visitClassDeclaration(
		classDeclaration: KSClassDeclaration,
		data: Unit
	): ClassStructure = classDeclaration.getClassStructure()
	
	/**
	 * 获取 ClassStructure
	 */
	private fun KSClassDeclaration.getClassStructure(): ClassStructure {
		val apiKSAnnotation = getKSAnnotationByType(ClassNames.Api)!!
		val className = ClassName("${packageName.asString()}.impls", "${simpleName.asString()}Impl")
		val superinterface = this.toClassName()
		val apiScopeClassName = apiKSAnnotation.getClassName("apiScope") ?: ClassNames.DefaultApiScope
		val apiScopeClassNames = apiKSAnnotation.getClassNames("apiScopes")?.toSet() ?: emptySet()
		val mergeApiScopeClassNames = when {
			apiScopeClassName == ClassNames.DefaultApiScope && apiScopeClassNames.isNotEmpty() -> apiScopeClassNames
			apiScopeClassName != ClassNames.DefaultApiScope && apiScopeClassNames.isNotEmpty() -> apiScopeClassNames + apiScopeClassName
			else -> setOf(apiScopeClassName)
		}
		apiKSAnnotation.compileCheck(ClassNames.ApiScope !in mergeApiScopeClassNames) {
			"${simpleName.asString()} 接口上的 @Api 注解的 apiScope 不允许使用 ApiScope::class，请使用默认的 DefaultApiScope::class 或者自定义 object 对象并实现 ApiScope::class"
		}
		val apiUrl = getApiUrl(apiKSAnnotation)
		val apiStructure = ApiStructure(apiUrl, mergeApiScopeClassNames)
		val funStructure = getFunStructures()
		return ClassStructure(className, superinterface, this.getVisibilityKModifier(), apiStructure, funStructure)
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
	
	/**
	 * 获取 `@Api` 注解上的 url 参数
	 */
	private fun KSClassDeclaration.getApiUrl(annotation: KSAnnotation): String? {
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
	
	/**
	 * 获取 FunStructures
	 */
	private fun KSClassDeclaration.getFunStructures(): List<FunStructure> {
		return this.getDeclaredFunctions().toList()
			.filter { it.isAbstract }
			.map {
				it.compileCheck(Modifier.SUSPEND in it.modifiers) {
					val funName = it.simpleName.asString()
					"$funName 函数缺少 suspend 修饰符"
				}
				val funName = it.simpleName.asString()
				val funModels = with(ModelResolvers) { it.getAllFunModels() }
				val isWebSocket = funModels.any { model -> model is WebSocketModel }
				val parameterModels = with(ModelResolvers) { it.getAllParameterModel(isWebSocket) }
				val returnStructure = it.getReturnStructure(isWebSocket)
				if (isWebSocket) {
					FunStructure(funName, returnStructure, parameterModels, funModels, emptyList())
				} else {
					val valueParameterModels = with(ModelResolvers) { it.getAllValueParameterModels() }
					FunStructure(funName, returnStructure, parameterModels, funModels, valueParameterModels)
				}
			}
	}
	
	/**
	 * 获取 ReturnStructure
	 */
	private fun KSFunctionDeclaration.getReturnStructure(isWebSocket: Boolean): ReturnStructure {
		val returnType = this.returnType!!
		val typeName = returnType.toTypeName()
		return if (isWebSocket) {
			returnType.compileCheck(!typeName.isNullable && typeName == ClassNames.Unit) {
				"${simpleName.asString()} 函数必须使用 ${ClassNames.Unit.canonicalName} 作为返回类型，因为你已经标记了 @WebSocket 注解"
			}
			UnitReturnStructure
		} else {
			val rawType = typeName.rawType
			val isResult = rawType == ClassNames.Result
			if (isResult) {
				returnType.compileCheck(!rawType.isNullable && typeName is ParameterizedTypeName) {
					"${simpleName.asString()} 函数不允许为 Result 返回类型设置为可空"
				}
				AnyReturnStructure(typeName, isResult = true, isUnit = false)
			} else {
				returnType.compileCheck(rawType != ClassNames.Nothing) {
					"${simpleName.asString()} 函数不允许使用 Nothing 返回类型"
				}
				val isUnit = rawType == ClassNames.Unit
				if (isUnit) {
					returnType.compileCheck(!rawType.isNullable) {
						"${simpleName.asString()} 函数不允许使用 Unit? 返回类型"
					}
				}
				AnyReturnStructure(typeName, isResult = false, isUnit = isUnit)
			}
		}
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): ClassStructure = error("Not Implemented")
}