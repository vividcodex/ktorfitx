package cn.vividcode.multiplatform.ktorfitx.ksp.visitor

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassName
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.rawType
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ApiStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.FunStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ReturnStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver.ModelResolvers
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility.INTERNAL
import com.google.devtools.ksp.symbol.Visibility.PUBLIC
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 16:17
 *
 * 文件介绍：ApiVisitor2
 */
internal class ApiVisitor(
	private val resolver: Resolver
) : KSEmptyVisitor<Unit, ClassStructure?>() {
	
	private companion object {
		
		private val urlRegex = "^\\S*[a-zA-Z0-9]+\\S*$".toRegex()
		
		private val apiClassName = ClassName("cn.vividcode.multiplatform.ktorfitx.annotation", "Api")
		private val apiScopeClassName by lazy {
			ClassName("cn.vividcode.multiplatform.ktorfitx.api.scope", "ApiScope")
		}
	}
	
	override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): ClassStructure? {
		return classDeclaration.getClassStructure()
	}
	
	/**
	 * 获取 ClassStructure
	 */
	private fun KSClassDeclaration.getClassStructure(): ClassStructure? {
		val apiKSAnnotation = getKSAnnotationByType(apiClassName) ?: return null
		val className = ClassName("${packageName.asString()}.impl", "${simpleName.asString()}Impl")
		val superinterface = this.toClassName()
		val apiScopeClassName = apiKSAnnotation.getClassName("apiScope") ?: apiScopeClassName
		val apiUrl = apiKSAnnotation.getValue<String>("url") ?: ""
		val apiStructure = ApiStructure(formatApiUrl(apiUrl, simpleName.asString()), apiScopeClassName)
		val funStructure = getFunStructures()
		return ClassStructure(className, superinterface, getKModifier(), apiStructure, funStructure)
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
				val parameterModels = with(ModelResolvers) { it.getAllParameterModel() }
				val valueParameterModels = with(ModelResolvers) { it.getAllValueParameterModels() }
				val functionModels = with(ModelResolvers) { it.getAllFunctionModels(resolver) }
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
				check(typeArgument is ClassName || typeArgument is ParameterizedTypeName) {
					"不支持的返回数据类型 $typeName"
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
		check(typeName.rawType.copy(nullable = false) in ReturnTypes.returnTypes) {
			"$typeName 不支持的类型"
		}
		return this
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): ClassStructure? = null
}