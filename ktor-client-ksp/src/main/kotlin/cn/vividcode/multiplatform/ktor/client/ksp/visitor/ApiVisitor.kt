package cn.vividcode.multiplatform.ktor.client.ksp.visitor

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
		val functionModels = getFunctionModels(classDeclaration)
		return ClassModel(className, superinterfaceClassName, functionModels)
	}
	
	/**
	 * 获取 FunctionModels
	 */
	private fun getFunctionModels(classDeclaration: KSClassDeclaration): List<FunctionModel> {
		val annotation = classDeclaration.getAnnotation(Api::class)!!
		var baseUrl = annotation.getArgumentValue(Api::baseUrl)?.trim() ?: ""
		if (baseUrl.isNotEmpty() && !baseUrl.startsWith("/")) {
			baseUrl = "/$baseUrl"
		}
		return classDeclaration.getAllFunctions().toList().mapNotNull {
			val requestTypeModel = getRequestTypeModel(it, baseUrl) ?: return@mapNotNull null
			if (!it.modifiers.contains(Modifier.SUSPEND)) {
				error("${it.qualifiedName!!.asString()} 方法缺少 suspend 关键字")
			}
			val parameter = it.parameters
			val functionName = it.simpleName.asString()
			FunctionModel(
				functionName,
				getParameterModels(parameter),
				getReturnTypeName(it),
				requestTypeModel,
				getQueryModels(parameter),
				getHeaderModels(parameter),
				getHeadersModels(it),
				getFormModels(parameter),
				getBodyModel(parameter)
			)
		}
	}
	
	/**
	 * 获取 ParameterModels
	 */
	private fun getParameterModels(valueParameters: List<KSValueParameter>): List<ParameterModel> {
		return valueParameters.map {
			val name = it.name!!.asString()
			val className = it.type.resolve().declaration.className
			ParameterModel(name, className)
		}
	}
	
	private fun getReturnTypeName(functionDeclaration: KSFunctionDeclaration): TypeName {
		return functionDeclaration.returnType!!.resolve().let {
			if (it.arguments.isEmpty()) {
				it.declaration.className
			} else {
				val parameterizedType = it.arguments.first().type!!.resolve()
				val typeArgument = parameterizedType.declaration.className.let {
					if (parameterizedType.arguments.isEmpty()) {
						it
					} else if (parameterizedType.declaration.qualifiedName?.asString() == List::class.qualifiedName) {
						it.parameterizedBy(parameterizedType.arguments.first().type!!.resolve().declaration.className)
					} else {
						error("不支持的类型：${parameterizedType.declaration.qualifiedName?.asString()}")
					}
				}
				it.declaration.className.parameterizedBy(typeArgument)
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
	private fun getRequestTypeModel(functionDeclaration: KSFunctionDeclaration, baseUrl: String): RequestTypeModel? {
		val requestTypes = functionDeclaration.getAnnotationSize(GET::class, POST::class, PUT::class, DELETE::class)
		val functionName = functionDeclaration.qualifiedName!!.asString()
		if (requestTypes > 1) error("$functionName 方法上只允许标记：@GET @POST @PUT @DELETE 中的一个")
		functionDeclaration.getAnnotation(GET::class)?.let {
			return parseRequestTypeModel(it, baseUrl, RequestType.GET)
		}
		functionDeclaration.getAnnotation(POST::class)?.let {
			return parseRequestTypeModel(it, baseUrl, RequestType.POST)
		}
		functionDeclaration.getAnnotation(PUT::class)?.let {
			return parseRequestTypeModel(it, baseUrl, RequestType.PUT)
		}
		functionDeclaration.getAnnotation(DELETE::class)?.let {
			return parseRequestTypeModel(it, baseUrl, RequestType.DELETE)
		}
		return null
	}
	
	/**
	 * 获取 FunctionParamModel
	 */
	private fun parseRequestTypeModel(annotation: KSAnnotation, baseUrl: String, type: RequestType): RequestTypeModel {
		var url = annotation.getArgumentValue<String>("url")?.trim() ?: ""
		if (url.isEmpty()) error("url为空")
		if (!url.startsWith("/")) {
			url = "/$url"
		}
		val auth = annotation.getArgumentValue<Boolean>("auth") ?: false
		return RequestTypeModel(type, baseUrl + url, auth)
	}
	
	/**
	 * 获取 QueryModel
	 */
	private fun getQueryModels(valueParameters: List<KSValueParameter>): List<QueryModel> {
		return valueParameters.mapNotNull {
			val queryAnnotation = it.getAnnotation(Query::class) ?: return@mapNotNull null
			val name = queryAnnotation.getArgumentValue(Query::name)?.trim() ?: ""
			if (name.isEmpty()) error("@Query 的 name 不允许为空")
			var sha256Layer = 0
			it.getAnnotation(SHA256::class)?.apply {
				sha256Layer = this.getArgumentValue(SHA256::layer) ?: 1
			}
			QueryModel(name, it.name!!.asString(), sha256Layer)
		}
	}
	
	/**
	 * 获取 HeaderModels
	 */
	private fun getHeaderModels(valueParameters: List<KSValueParameter>): List<HeaderModel> {
		return valueParameters.mapNotNull {
			val annotation = it.getAnnotation(Header::class) ?: return@mapNotNull null
			val name = annotation.getArgumentValue(Header::name)?.trim() ?: ""
			if (name.isEmpty()) error("@Header 的 name 不允许为空")
			var sha256Layer = 0
			it.getAnnotation(SHA256::class)?.apply {
				sha256Layer = this.getArgumentValue(SHA256::layer) ?: 1
			}
			HeaderModel(name, it.name!!.asString(), sha256Layer)
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
	 * 获取 FormModels
	 */
	private fun getFormModels(valueParameters: List<KSValueParameter>): List<FormModel> {
		return valueParameters.mapNotNull {
			val annotation = it.getAnnotation(Form::class) ?: return@mapNotNull null
			val name = annotation.getArgumentValue(Form::name)?.trim() ?: ""
			if (name.isEmpty()) error("@Form 的 name 不允许为 null")
			var sha256Layer = 0
			it.getAnnotation(SHA256::class)?.apply {
				sha256Layer = this.getArgumentValue(SHA256::layer) ?: 1
			}
			FormModel(name, it.name!!.asString(), sha256Layer)
		}
	}
	
	/**
	 * 获取 BodyModel
	 */
	private fun getBodyModel(valueParameters: List<KSValueParameter>): BodyModel? {
		var bodyModel: BodyModel? = null
		valueParameters.forEach {
			if (bodyModel != null) {
				error("@Body 只允许标记在一个参数上")
			}
			val annotation = it.getAnnotation(Body::class)
			if (annotation != null) {
				bodyModel = BodyModel(it.name!!.asString())
			}
		}
		return bodyModel
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): KSClassDeclaration? {
		error("未使用")
	}
}