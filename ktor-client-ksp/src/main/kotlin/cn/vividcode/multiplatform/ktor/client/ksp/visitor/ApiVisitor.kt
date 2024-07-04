package cn.vividcode.multiplatform.ktor.client.ksp.visitor

import cn.vividcode.multiplatform.ktor.client.api.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.annotation.Api
import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getArgumentClassName
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getArgumentValue
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.ApiStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.ClassStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.FunStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.ReturnStructure
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver.ModelResolvers
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午4:17
 *
 * 介绍：ApiVisitor2
 */
internal class ApiVisitor : KSEmptyVisitor<Unit, ClassStructure?>() {
	
	private companion object {
		
		private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
		
		private val legalReturnQualifiedNames = arrayOf(Unit::class.qualifiedName, ByteArray::class.qualifiedName, ResultBody::class.qualifiedName)
	}
	
	override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): ClassStructure? {
		return classDeclaration.getClassStructure()
	}
	
	/**
	 * 获取 ClassStructure
	 */
	private fun KSClassDeclaration.getClassStructure(): ClassStructure? {
		val apiKSAnnotation = getKSAnnotationByType(Api::class) ?: return null
		val className = ClassName("${packageName.asString()}.impl", "${simpleName.asString()}Impl")
		val superinterface = this.toClassName()
		val apiScopeClassName = apiKSAnnotation.getArgumentClassName(Api::apiScope) ?: ApiScope::class.asClassName()
		val apiUrl = apiKSAnnotation.getArgumentValue(Api::url) ?: ""
		val apiStructure = ApiStructure(formatApiUrl(apiUrl, simpleName.asString()), apiScopeClassName)
		val funStructures = getFunStructures()
		return ClassStructure(className, superinterface, apiStructure, funStructures)
	}
	
	/**
	 * 格式话 apiUrl
	 */
	private fun formatApiUrl(apiUrl: String, className: String): String {
		if (apiUrl.isBlank() || apiUrl == "/") return ""
		check(urlRegex.matches(apiUrl)) {
			"$className 的 url 参数格式错误"
		}
		return if (apiUrl.startsWith('/')) apiUrl else "/$apiUrl"
	}
	
	/**
	 * 获取 FunStructures
	 */
	private fun KSClassDeclaration.getFunStructures(): Sequence<FunStructure> {
		return this.getAllFunctions()
			.filter { it.isAbstract }
			.map {
				check(Modifier.SUSPEND in it.modifiers) { "${qualifiedName!!.asString()} 方法缺少 suspend 修饰" }
				val funName = it.simpleName.asString()
				val returnType = it.getReturnStructure().checkLegal()
				val valueParameterModels = ModelResolvers.getValueParameterModels(it)
				val functionModels = ModelResolvers.getFunctionModels(it)
				FunStructure(funName, returnType, functionModels, valueParameterModels)
			}
	}
	
	/**
	 * 获取 ReturnStructure
	 */
	private fun KSFunctionDeclaration.getReturnStructure(): ReturnStructure {
		val type = returnType!!.resolve()
		val className = (type.declaration as KSClassDeclaration).toClassName()
		if (type.arguments.isEmpty()) {
			return ReturnStructure(className)
		}
		val parameterizedType = type.arguments.first().type!!.resolve()
		val parameterizedClassName = (parameterizedType.declaration as KSClassDeclaration).toClassName()
		if (parameterizedType.arguments.isEmpty()) {
			return ReturnStructure(className, parameterizedClassName)
		}
		if (parameterizedType.declaration.qualifiedName?.asString() == List::class.qualifiedName) {
			val parameterizedClassName2 = (parameterizedType.arguments.first().type!!.resolve().declaration as KSClassDeclaration).toClassName()
			return ReturnStructure(className, parameterizedClassName, parameterizedClassName2)
		}
		error("不支持的类型：${parameterizedType.declaration.qualifiedName?.asString()}")
	}
	
	/**
	 * 检查 ReturnStructure 合法
	 */
	private fun ReturnStructure.checkLegal(): ReturnStructure {
		val qualifiedName = this.className.toString()
		check(qualifiedName in legalReturnQualifiedNames) {
			"$qualifiedName 不支持的类型"
		}
		return this
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): ClassStructure? = null
}