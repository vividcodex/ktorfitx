package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.*
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ExceptionListenerModel
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 15:43
 *
 * 文件介绍：ExceptionListenerResolver
 */
internal object ExceptionListenerResolver {
	
	private val exceptionListenersClassName = ClassName.bestGuess(KtorfitxQualifiers.EXCEPTION_LISTENERS)
	
	fun KSFunctionDeclaration.resolves(resolver: Resolver): List<ExceptionListenerModel> {
		val annotation = getKSAnnotationByType(exceptionListenersClassName) ?: return emptyList()
		
		val listenerClassNames = mutableListOf<ClassName>()
		listenerClassNames += annotation.getClassName("listener")!!
		annotation.getClassNames("listeners")?.let { listenerClassNames += it }
		val exceptionListenerModels = listenerClassNames.map {
			annotation.compileCheck(it.canonicalName != KtorfitxQualifiers.EXCEPTION_LISTENER) {
				val funName = this.simpleName.asString()
				"$funName 方法上的 @ExceptionListeners 注解中不能使用 ExceptionListener::class，请实现它并且必须是非 private 访问权限的 object 类型的"
			}
			val classDeclaration = resolver.getClassDeclarationByName(it.canonicalName)!!
			val classKind = classDeclaration.classKind
			classDeclaration.compileCheck(classKind == ClassKind.OBJECT) {
				"${it.simpleName} 类不允许使用 ${classKind.code} 类型，请使用 object 类型"
			}
			classDeclaration.compileCheck(!classDeclaration.modifiers.contains(Modifier.PRIVATE)) {
				"${it.simpleName} 类不允许使用 private 访问权限"
			}
			val typeArguments = classDeclaration.superTypes.first().element!!.typeArguments
			val exceptionTypeName = typeArguments[0].toTypeName()
			val returnTypeName = typeArguments[1].toTypeName()
			val funReturnTypeName = this.returnType!!.toTypeName().copy(nullable = false)
			annotation.compileCheck(returnTypeName == ReturnTypes.unitClassName || returnTypeName == funReturnTypeName) {
				val funName = this.simpleName.asString()
				val returnSimpleNames = if (returnTypeName.simpleName != funReturnTypeName.simpleName) {
					returnTypeName.simpleName to funReturnTypeName.simpleName
				} else {
					returnTypeName.rawType.canonicalName to funReturnTypeName.rawType.canonicalName
				}
				"$funName 方法上的 @ExceptionListeners 注解中的 ${it.simpleName} 监听器的返回类型 ${returnSimpleNames.first} 与方法中的返回类型 ${returnSimpleNames.second} 不一致"
			}
			ExceptionListenerModel(it, exceptionTypeName, returnTypeName)
		}
		exceptionListenerModels.groupBy { it.listenerClassName }.forEach { (className, models) ->
			annotation.compileCheck(models.size == 1) {
				val funName = this.simpleName.asString()
				"$funName 方法上的 @ExceptionListeners 中存在相同的监听器 ${className.simpleName}"
			}
		}
		exceptionListenerModels.groupBy { it.exceptionTypeName }.forEach { (className, models) ->
			annotation.compileCheck(models.size == 1) {
				val funName = this.simpleName.asString()
				val listeners = models.joinToString { it.listenerClassName.simpleName }
				"$funName 方法上的 @ExceptionListeners 中的监听器 $listeners 中同时处理了相同的异常 ${className.simpleName}"
			}
		}
		return exceptionListenerModels
	}
}