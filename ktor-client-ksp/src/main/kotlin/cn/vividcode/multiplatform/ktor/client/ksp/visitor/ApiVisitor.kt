package cn.vividcode.multiplatform.ktor.client.ksp.visitor

import cn.vividcode.multiplatform.ktor.client.api.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.annotation.Api
import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getArgumentClassName
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getArgumentValue
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.ApiStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.FunStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.ReturnStructure
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver.FunctionModelResolver
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver.ParameterModelResolver
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver.ValueParameterModelResolver
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility.INTERNAL
import com.google.devtools.ksp.symbol.Visibility.PUBLIC
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

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
		
		private val legalReturnTypeNames = arrayOf(
			Unit::class.asTypeName(),
			ByteArray::class.asTypeName(),
			ResultBody::class.asTypeName()
		)
		
		private val listTypeName = List::class.asTypeName()
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
		return ClassStructure(className, superinterface, getKModifier(), apiStructure, funStructures)
	}
	
	/**
	 * 获取接口访问级别
	 */
	private fun KSClassDeclaration.getKModifier(): KModifier {
		return this.getVisibility().let {
			when (it) {
				PUBLIC -> KModifier.PUBLIC
				INTERNAL -> KModifier.INTERNAL
				else -> error("被 @Api 标记的接口访问权限只允许 public 和 internal")
			}
		}
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
				check(Modifier.SUSPEND in it.modifiers) { "${it.qualifiedName!!.asString()} 方法缺少 suspend 修饰" }
				val funName = it.simpleName.asString()
				val returnType = it.getReturnStructure().checkLegal()
				val parameterModels = ParameterModelResolver.resolves(it)
				val valueParameterModels = ValueParameterModelResolver.resolves(it)
				val functionModels = FunctionModelResolver.resolves(it)
				FunStructure(funName, returnType, parameterModels, functionModels, valueParameterModels)
			}
	}
	
	/**
	 * 获取 ReturnStructure
	 */
	private fun KSFunctionDeclaration.getReturnStructure(): ReturnStructure {
		val typeName = returnType!!.resolve().toTypeName()
		return when (typeName) {
			is ClassName -> ReturnStructure(typeName)
			is ParameterizedTypeName -> {
				check(typeName.typeArguments.size == 1) {
					"不支持的返回数据类型 $typeName"
				}
				val typeArgument = typeName.typeArguments.first()
				check(typeArgument is ClassName || typeArgument is WildcardTypeName || (typeArgument is ParameterizedTypeName && typeArgument.rawType == listTypeName)) {
					"不支持的返回数据类型 $typeName" + typeArgument::class
				}
				ReturnStructure(typeName)
			}
			
			else -> error("不支持的返回数据类型 $typeName")
		}
	}
	
	/**
	 * 检查 ReturnStructure 合法
	 */
	private fun ReturnStructure.checkLegal(): ReturnStructure {
		check(rawType in legalReturnTypeNames) {
			"$typeName 不支持的类型"
		}
		return this
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): ClassStructure? = null
}