package cn.vividcode.multiplatform.ktorfitx.ksp.visitor

import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassName
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassNames
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ApiStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.FunStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ReturnStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver.ModelResolvers
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.Visibility.INTERNAL
import com.google.devtools.ksp.symbol.Visibility.PUBLIC
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 16:17
 *
 * 文件介绍：ApiVisitor
 */
internal class ApiVisitor(
	private val resolver: Resolver,
) : KSEmptyVisitor<Unit, VisitorResult?>() {
	
	private companion object {
		
		private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
		
		private val apiClassName = ClassName.bestGuess(KtorfitxQualifiers.API)
		private val apiScopeClassName by lazy { ClassName.bestGuess(KtorfitxQualifiers.API_SCOPE) }
		private val defaultApiScopeClassName by lazy { ClassName.bestGuess(KtorfitxQualifiers.DEFAULT_API_SCOPE) }
	}
	
	override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): VisitorResult? {
		val classStructure = classDeclaration.getClassStructure() ?: return null
		return VisitorResult(classStructure)
	}
	
	/**
	 * 获取 ClassStructure
	 */
	private fun KSClassDeclaration.getClassStructure(): ClassStructure? {
		val apiKSAnnotation = getKSAnnotationByType(apiClassName) ?: return null
		val className = ClassName("${packageName.asString()}.impl", "${simpleName.asString()}Impl")
		val superinterface = this.toClassName()
		val apiScopeClassName = apiKSAnnotation.getClassName("apiScope") ?: defaultApiScopeClassName
		val apiScopeClassNames = apiKSAnnotation.getClassNames("apiScopes")?.toSet() ?: emptySet()
		val mergeApiScopeClassNames = when {
			apiScopeClassName == defaultApiScopeClassName && apiScopeClassNames.isNotEmpty() -> apiScopeClassNames
			apiScopeClassName != defaultApiScopeClassName && apiScopeClassNames.isNotEmpty() -> apiScopeClassNames + apiScopeClassName
			else -> setOf(apiScopeClassName)
		}
		apiKSAnnotation.compileCheck(ApiVisitor.apiScopeClassName !in mergeApiScopeClassNames) {
			val simpleName = this.simpleName.asString()
			"$simpleName 接口上的 @Api 注解的 apiScope 不允许使用 ApiScope::class，请使用默认的 DefaultApiScope::class 或者自定义 object 对象并实现 ApiScope::class"
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
			val className = this.simpleName.asString()
			"$className 接口标记了 @Api，所以必须是 public 或 internal 访问权限的"
		}
		return KModifier.entries.first { it.name == visibility.name }
	}
	
	/**
	 * 获取 `@Api` 注解上的 url 参数
	 */
	private fun KSClassDeclaration.getApiUrl(annotation: KSAnnotation): String {
		val apiUrl = annotation.getValue<String>("url")
			?: return ""
		if (apiUrl.isBlank() || apiUrl == "/") return ""
		annotation.compileCheck(urlRegex.matches(apiUrl)) {
			val className = this.simpleName.asString()
			"$className 接口上的 @Api 注解的 url 参数格式错误"
		}
		return if (apiUrl.startsWith("/")) apiUrl else "/$apiUrl"
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
					"$funName 方法缺少 suspend 修饰符"
				}
				val funName = it.simpleName.asString()
				val returnStructure = it.getReturnStructure()
				val parameterModels = with(ModelResolvers) { it.getAllParameterModel() }
				val valueParameterModels = with(ModelResolvers) { it.getAllValueParameterModels() }
				val functionModels = with(ModelResolvers) { it.getAllFunctionModels(resolver) }
				FunStructure(funName, returnStructure, parameterModels, functionModels, valueParameterModels)
			}
	}
	
	/**
	 * 获取 ReturnStructure
	 */
	private fun KSFunctionDeclaration.getReturnStructure(): ReturnStructure {
		val returnType = this.returnType!!
		val typeName = returnType.toTypeName()
		val lazyErrorMessage = {
			val funName = this.simpleName.asString()
			val returnTypes = ReturnTypes.returnTypes.joinToString()
			"$funName 方法的返回类型 $typeName 不支持，请使用 $returnTypes"
		}
		returnType.compileCheck(
			value = typeName is ClassName || typeName is ParameterizedTypeName,
			lazyErrorMessage = lazyErrorMessage
		)
		
		if (typeName is ParameterizedTypeName) {
			val arguments = typeName.typeArguments
			returnType.compileCheck(
				value = arguments.size == 1 && (arguments[0] is ClassName || arguments[0] is ParameterizedTypeName),
				lazyErrorMessage = lazyErrorMessage
			)
		}
		val returnStructure = ReturnStructure(typeName)
		returnType.compileCheck(
			value = returnStructure.notNullRawType in ReturnTypes.returnTypes,
			lazyErrorMessage = lazyErrorMessage
		)
		return returnStructure
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): VisitorResult? = null
}