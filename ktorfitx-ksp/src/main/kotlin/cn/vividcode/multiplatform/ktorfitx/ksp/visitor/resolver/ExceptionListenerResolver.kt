package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassName
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassNames
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ExceptionListenerModel
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 15:43
 *
 * 文件介绍：ExceptionHandlerResolver
 */
internal object ExceptionListenerResolver {
	
	private val exceptionListenersClassName = ClassName("cn.vividcode.multiplatform.ktorfitx.annotation", "ExceptionListeners")
	
	private const val EXCEPTION_LISTENER_QUALIFIED_NAME = "cn.vividcode.multiplatform.ktorfitx.api.exception.ExceptionListener"
	
	fun KSFunctionDeclaration.resolves(resolver: Resolver): List<ExceptionListenerModel> {
		val annotation = getKSAnnotationByType(exceptionListenersClassName) ?: return emptyList()
		
		val listenerClassNames = mutableListOf<ClassName>()
		listenerClassNames += annotation.getClassName("listener")!!
		listenerClassNames += annotation.getClassNames("listeners") ?: emptyArray()
		return listenerClassNames.map {
			getExceptionListenerModel(resolver, it)
		}.check(this)
	}
	
	private fun KSFunctionDeclaration.getExceptionListenerModel(resolver: Resolver, listenerClassName: ClassName): ExceptionListenerModel {
		check(listenerClassName.canonicalName != EXCEPTION_LISTENER_QUALIFIED_NAME) {
			"${this.simpleName.asString()} 的 @ExceptionListeners 注解的参数 listener 不能使用 ExceptionListener::class"
		}
		val classDeclaration = resolver.getClassDeclarationByName(listenerClassName.canonicalName)!!
		check(classDeclaration.classKind == ClassKind.OBJECT) {
			"${classDeclaration.simpleName.asString()} 的 ClassKind 只允许是 ${ClassKind.OBJECT} 类型的"
		}
		val typeArguments = classDeclaration.superTypes.first().element!!.typeArguments
		val exceptionTypeName = typeArguments.first().toTypeName()
		val returnTypeName = typeArguments[1].toTypeName()
		return ExceptionListenerModel(listenerClassName, exceptionTypeName, returnTypeName)
	}
	
	private fun List<ExceptionListenerModel>.check(functionDeclaration: KSFunctionDeclaration): List<ExceptionListenerModel> {
		val funName = functionDeclaration.simpleName.asString()
		this.groupBy { it.listenerClassName }.forEach { (listener, it) ->
			check(it.size == 1) {
				"$funName 方法的 @ExceptionListeners 中存在相同的异常监听器: ${listener.simpleName}"
			}
		}
		this.groupBy { it.exceptionTypeName }.forEach { (exception, it) ->
			check(it.size == 1) {
				val listeners = it.joinToString { it.listenerClassName.simpleName }
				"$funName 方法的 @ExceptionListeners 中的异常监听器 $listeners 有相同的异常: ${exception.simpleName}"
			}
		}
		val returnType = functionDeclaration.returnType!!.toTypeName().copy(nullable = false)
		this.forEach {
			check(it.returnTypeName == returnType || it.returnTypeName == ReturnTypes.unitClassName) {
				"$funName 方法的 @ExceptionListeners 中的异常监听器 ${it.listenerClassName.simpleName} 的返回类型 ${it.returnTypeName.simpleName} 与方法的返回类型 ${returnType.simpleName} 不一致"
			}
		}
		return this
	}
}