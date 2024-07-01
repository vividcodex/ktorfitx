package cn.vividcode.multiplatform.ktor.client.ksp.visitor

import cn.vividcode.multiplatform.ktor.client.api.ApiScope
import cn.vividcode.multiplatform.ktor.client.api.annotation.*
import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.expends.*
import cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.KtorApiKotlinPoet
import cn.vividcode.multiplatform.ktor.client.ksp.model.*
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/11 下午11:41
 *
 * 介绍：ApiVisitor2
 */
internal class ApiVisitor(
	private val codeGenerator: CodeGenerator
) : KSEmptyVisitor<Unit, KSClassDeclaration?>() {
	
	private val ktorApiKotlinPoet by lazy { KtorApiKotlinPoet() }
	
	private companion object {
		private val apiScopeClassName = ApiScope::class.asClassName()
	}
	
	override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): KSClassDeclaration {
		val classModel = getClassModel(classDeclaration)
		val fileSpec = ktorApiKotlinPoet.getFileSpec(classModel)
		codeGenerator.generate(fileSpec, classModel.className.packageName, classModel.className.simpleName)
		return classDeclaration
	}
	
	/**
	 * 获取 ClassModel
	 */
	private fun getClassModel(classDeclaration: KSClassDeclaration): ClassModel {
		val className = classDeclaration.className.let { ClassName("${it.packageName}.impl", "${it.simpleName}Impl") }
		val superinterfaceClassName = classDeclaration.className
		val apiScopeClassName = classDeclaration.getAnnotation(Api::class)!!.getArgumentClassName(Api::apiScope) ?: apiScopeClassName
		val functionModels = getFunctionModels(classDeclaration)
		return ClassModel(className, superinterfaceClassName, apiScopeClassName, functionModels)
	}
	
	/**
	 * 获取 FunctionModels
	 */
	private fun getFunctionModels(classDeclaration: KSClassDeclaration): List<FunctionModel> {
		val annotation = classDeclaration.getAnnotation(Api::class)!!
		var baseUrl = annotation.getArgumentValue(Api::url)?.trim() ?: ""
		if (baseUrl.isNotEmpty() && !baseUrl.startsWith("/")) {
			baseUrl = "/$baseUrl"
		}
		return classDeclaration.getAllFunctions().toList().mapNotNull {
			val (requestType, url, auth) = getRequestModel(it, baseUrl) ?: return@mapNotNull null
			check(Modifier.SUSPEND in it.modifiers) {
				"${it.qualifiedName!!.asString()} 方法缺少 suspend 关键字"
			}
			with(it.parameters) {
				FunctionModel(
					it.simpleName.asString(),
					requestType,
					url,
					auth,
					getParameterModels(),
					getReturnTypeName(it),
					getQueryModels(),
					getHeaderModels(),
					getHeadersModels(it),
					getFormModels(),
					getPathModels(),
					getBodyModel()
				)
			}
		}
	}
	
	/**
	 * 获取 ParameterModels
	 */
	private fun List<KSValueParameter>.getParameterModels(): List<ParameterModel> {
		return this.map {
			val name = it.name!!.asString()
			val className = it.type.resolve().declaration.className
			ParameterModel(name, className)
		}
	}
	
	private fun getReturnTypeName(functionDeclaration: KSFunctionDeclaration): TypeName {
		return functionDeclaration.returnType!!.resolve().let { type ->
			if (type.arguments.isEmpty()) {
				type.declaration.className
			} else {
				val parameterizedType = type.arguments.first().type!!.resolve()
				val typeArgument = parameterizedType.declaration.className.let { className ->
					if (parameterizedType.arguments.isEmpty()) {
						className
					} else if (parameterizedType.declaration.qualifiedName?.asString() == List::class.qualifiedName) {
						className.parameterizedBy(parameterizedType.arguments.first().type!!.resolve().declaration.className)
					} else {
						error("不支持的类型：${parameterizedType.declaration.qualifiedName?.asString()}")
					}
				}
				type.declaration.className.parameterizedBy(typeArgument)
			}
		}.also {
			val qualifiedName = it.toString().split("<").first()
			when (qualifiedName) {
				Unit::class.qualifiedName -> {}
				ByteArray::class.qualifiedName -> {}
				ResultBody::class.qualifiedName!! -> {}
				else -> error("不支持的类型")
			}
		}
	}
	
	/**
	 * 获取 FunctionParamModel
	 */
	private fun getRequestModel(
		functionDeclaration: KSFunctionDeclaration,
		baseUrl: String
	): Triple<KClass<out Annotation>, String, Boolean>? {
		val requestTypeClasses = arrayOf(GET::class, POST::class, PUT::class, DELETE::class, OPTIONS::class, PATCH::class, HEAD::class)
		val requestTypes = functionDeclaration.getAnnotationSize(*requestTypeClasses)
		val functionName = functionDeclaration.qualifiedName!!.asString()
		if (requestTypes > 1) {
			error("$functionName 方法上只允许标记：@GET @POST @PUT @DELETE @OPTIONS @PATCH @HEAD 中的一个")
		}
		requestTypeClasses.forEach {
			functionDeclaration.getAnnotation(it)?.apply {
				val (url, auth) = parseRequestModel(this, baseUrl)
				return Triple(it, url, auth)
			}
		}
		return null
	}
	
	/**
	 * 获取 FunctionParamModel
	 */
	private fun parseRequestModel(annotation: KSAnnotation, baseUrl: String): Pair<String, Boolean> {
		var url = annotation.getArgumentValue<String>("url")?.trim() ?: ""
		if (url.isEmpty()) error("url 为空")
		if (url.all { it == '/' || it == ' ' }) error("url 格式错误")
		if (!url.startsWith("/")) {
			url = "/$url"
		}
		val auth = annotation.getArgumentValue<Boolean>("auth") ?: false
		return Pair(baseUrl + url, auth)
	}
	
	/**
	 * 获取 QueryModel
	 */
	private fun List<KSValueParameter>.getQueryModels(): List<QueryModel> {
		return this.getModels(Query::name, ::QueryModel)
	}
	
	/**
	 * 获取 HeaderModels
	 */
	private fun List<KSValueParameter>.getHeaderModels(): List<HeaderModel> {
		return this.getModels(Header::name, ::HeaderModel)
	}
	
	/**
	 * 获取 FormModels
	 */
	private fun List<KSValueParameter>.getFormModels(): List<FormModel> {
		return this.getModels(Form::name, ::FormModel)
	}
	
	/**
	 * 获取 PathModels
	 */
	private fun List<KSValueParameter>.getPathModels(): List<PathModel> {
		return this.getModels(Path::name, ::PathModel)
	}
	
	/**
	 * 解析 Form Query Path Header
	 */
	private inline fun <reified A : Annotation, T> List<KSValueParameter>.getModels(
		nameKProperty: KProperty1<A, String>,
		newModel: (name: String, variableName: String, sha256Layer: Int) -> T
	): List<T> {
		return this.mapNotNull {
			val annotation = it.getAnnotation(A::class) ?: return@mapNotNull null
			val variableName = it.name!!.asString()
			var name = annotation.getArgumentValue(nameKProperty)
			if (name.isNullOrBlank()) {
				name = variableName
			}
			var sha256Layer = 0
			it.getAnnotation(SHA256::class)?.apply {
				sha256Layer = this.getArgumentValue(SHA256::layer) ?: 1
			}
			newModel(name, variableName, sha256Layer)
		}
	}
	
	/**
	 * 获取 HeadersModels
	 */
	private fun getHeadersModels(functionDeclaration: KSFunctionDeclaration): List<HeadersModel> {
		val annotation = functionDeclaration.getAnnotation(Headers::class) ?: return emptyList()
		val argumentValue = annotation.getArgumentValue(Headers::values) ?: return emptyList()
		return argumentValue.map {
			val split = it.split(":")
			if (split.size != 2) error("@Headers 的名称和值由 : 分割")
			HeadersModel(split[0].trim(), split[1].trim())
		}
	}
	
	/**
	 * 获取 BodyModel
	 */
	private fun List<KSValueParameter>.getBodyModel(): BodyModel? {
		val bodyModels = this.mapNotNull {
			if (it.hasAnnotation(Body::class)) {
				BodyModel(it.name!!.asString())
			} else null
		}
		if (bodyModels.size > 1) {
			error("@Body 只允许标记在一个参数上")
		}
		return bodyModels.firstOrNull()
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): KSClassDeclaration? {
		error("未使用")
	}
}