package cn.vividcode.multiplatform.ktor.client.ksp.visitor

import cn.vividcode.multiplatform.ktor.client.api.annotation.*
import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.expends.*
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/3/23 22:19
 *
 * 介绍：Ktor代码生成器
 */
internal class ApiVisitor(
	private val codeGenerator: CodeGenerator,
	private val kspLogger: KSPLogger,
	private val namespace: String
) : KSEmptyVisitor<Unit, KSClassDeclaration?>() {
	
	companion object {
		private val RESULT_BODY_QUALIFIED_NAME: String = ResultBody::class.qualifiedName!!
		
		private val ktorClientClassName = ClassName("cn.vividcode.multiplatform.ktor.client.api", "KtorClient")
	}
	
	private val useImports by lazy { mutableMapOf<String, MutableSet<String>>() }
	
	private val ResultBody get() = use("cn.vividcode.multiplatform.ktor.client.api.model", "ResultBody")
	
	private val sha256 get() = use("cn.vividcode.multiplatform.ktor.client.api.expends", "sha256")
	
	private val body get() = use("io.ktor.client.call", "body")
	
	private val get get() = use("io.ktor.client.request", "get")
	
	private val post get() = use("io.ktor.client.request", "post")
	
	private val put get() = use("io.ktor.client.request", "put")
	
	private val delete get() = use("io.ktor.client.request", "delete")
	
	private val parameter get() = use("io.ktor.client.request", "parameter")
	
	private val setBody get() = use("io.ktor.client.request", "setBody")
	
	private val bearerAuth get() = use("io.ktor.client.request", "bearerAuth")
	
	private val MultiPartFormDataContent get() = use("io.ktor.client.request.forms", "MultiPartFormDataContent")
	
	private val formData get() = use("io.ktor.client.request.forms", "formData")
	
	private val readBytes get() = use("io.ktor.client.statement", "readBytes")
	
	private val ContentTypeClass get() = use("io.ktor.http", "ContentType")
	
	private val contentType get() = use("io.ktor.http", "contentType")
	
	private val isSuccess get() = use("io.ktor.http", "isSuccess")
	
	private val TextContent get() = use("io.ktor.http.content", "TextContent")
	
	private val encodeToString get() = use("kotlinx.serialization", "encodeToString")
	
	private val Json get() = use("kotlinx.serialization.json", "Json")
	
	private fun use(packageName: String, simpleName: String): String {
		val simpleNames = useImports.getOrPut(packageName) { mutableSetOf() }
		simpleNames += simpleName
		return simpleName
	}
	
	override fun visitClassDeclaration(
		classDeclaration: KSClassDeclaration,
		data: Unit
	): KSClassDeclaration? {
		val simpleName = classDeclaration.simpleName.asString()
		val className = ClassName("$namespace.api.generate.impl", "${simpleName}Impl")
		
		val typeSpec = getTypeSpec(classDeclaration, className) ?: return null
		
		val fileSpecBuilder = FileSpec.builder(className)
			.indent("\t")
			.addType(typeSpec)
		
		useImports.forEach(fileSpecBuilder::addImport)
		
		val fileSpec = fileSpecBuilder.build()
		
		codeGenerator.generate(fileSpec, className.packageName, className.simpleName)
		
		useImports.clear()
		
		return classDeclaration
	}
	
	private fun getTypeSpec(classDeclaration: KSClassDeclaration, className: ClassName): TypeSpec? {
		val funSpecs = getFunSpecs(classDeclaration) ?: return null
		val primaryConstructor = FunSpec.constructorBuilder()
			.addParameter("ktorClient", ktorClientClassName)
			.build()
		val propertySpec = PropertySpec.builder("ktorClient", ktorClientClassName)
			.addModifiers(KModifier.PRIVATE)
			.mutable(false)
			.initializer("ktorClient")
			.build()
		return TypeSpec.classBuilder(className)
			.addModifiers(KModifier.PUBLIC)
			.addSuperinterface(classDeclaration.className)
			.primaryConstructor(primaryConstructor)
			.addProperty(propertySpec)
			.addType(companionObjectBuilder(classDeclaration, className))
			.addFunctions(funSpecs)
			.build()
	}
	
	private fun companionObjectBuilder(classDeclaration: KSClassDeclaration, className: ClassName): TypeSpec {
		val returnClassName = classDeclaration.className
		val propertySpec = PropertySpec.builder("instance", returnClassName.copy(nullable = true))
			.addModifiers(KModifier.PRIVATE)
			.initializer("null")
			.mutable(true)
			.build()
		val codeBlock = CodeBlock.builder()
			.beginControlFlow("return instance ?: ${className.simpleName}(ktorClient).also")
			.addStatement("instance = it")
			.endControlFlow()
			.build()
		val functionSpec = FunSpec.builder("getInstance")
			.addModifiers(KModifier.PUBLIC)
			.returns(returnClassName)
			.addParameter("ktorClient", ktorClientClassName)
			.addCode(codeBlock)
			.build()
		return TypeSpec.companionObjectBuilder()
			.addProperty(propertySpec)
			.addFunction(functionSpec)
			.build()
	}
	
	private fun getFunSpecs(classDeclaration: KSClassDeclaration): List<FunSpec>? {
		val annotation = classDeclaration.getAnnotation(Api::class) ?: return null
		val baseUrl = annotation.getArgumentValue(Api::baseUrl)!!
		return classDeclaration.getAllFunctions().mapNotNull {
			getFunSpec(baseUrl, it)
		}.toList()
	}
	
	private fun getFunSpec(
		baseUrl: String,
		functionDeclaration: KSFunctionDeclaration
	): FunSpec? {
		val functionInfo = functionDeclaration.let {
			val size = it.getAnnotationSize(GET::class, POST::class, PUT::class, DELETE::class)
			if (size == 0) {
				return null
			}
			if (size > 1) {
				error("只能在方法上标记 @GET, @POST, @PUT, @DELETE 中的一个")
			}
			it.getFunctionInfo(GET::class, GET::url, GET::auth)
				?: it.getFunctionInfo(POST::class, POST::url, POST::auth)
				?: it.getFunctionInfo(PUT::class, PUT::url, PUT::auth)
				?: it.getFunctionInfo(DELETE::class, DELETE::url, DELETE::auth)
				?: return null
		}
		if (!functionDeclaration.modifiers.contains(Modifier.SUSPEND)) {
			kspLogger.error("${functionDeclaration.qualifiedName!!.asString()} 方法缺少 suspend 修饰")
			return null
		}
		val returnClassName = functionDeclaration.returnType!!.resolve().let {
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
						kspLogger.error("不支持的类型：${parameterizedType.declaration.qualifiedName?.asString()}")
						return null
					}
				}
				it.declaration.className.parameterizedBy(typeArgument)
			}
		}
		val valueParameterInfos = functionDeclaration.parameters.mapNotNull {
			it.getValueParameterInfo(Body::class)
				?: it.getValueParameterInfo(Query::class, Query::name)
				?: it.getValueParameterInfo(Form::class, Form::name)
				?: it.getValueParameterInfo(Header::class, Header::name)
		}
		val parameterSpecs = valueParameterInfos.map {
			ParameterSpec.builder(it.parameterName, it.className)
				.build()
		}
		val funSpec = FunSpec.builder(functionDeclaration.simpleName.asString())
			.addParameters(parameterSpecs)
			.returns(returnClassName)
			.addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
			.addCode(getCodeBlock(baseUrl, functionInfo, valueParameterInfos, returnClassName))
		return funSpec.build()
	}
	
	private fun getCodeBlock(
		baseUrl: String,
		functionInfo: FunctionInfo,
		valueParameterInfos: List<ValueParameterInfo>,
		returnClassName: TypeName
	): CodeBlock {
		val returnQualifiedName = returnClassName.toString().split("<").first()
		val isReturn = returnQualifiedName != Unit::class.qualifiedName
		val codeBlock = CodeBlock.builder()
			.beginControlFlow("${if (isReturn) "return " else ""}try {")
		val httpClient = (if (isReturn) "val response = " else "") + "ktorClient.httpClient"
		when (functionInfo.type) {
			GET::class -> {
				codeBlock.beginControlFlow("$httpClient.$get(urlString = \"\${ktorClient.domain}$baseUrl${functionInfo.url}\")")
			}
			
			POST::class -> {
				codeBlock.beginControlFlow("$httpClient.$post(urlString = \"\${ktorClient.domain}$baseUrl${functionInfo.url}\")")
			}
			
			PUT::class -> {
				codeBlock.beginControlFlow("$httpClient.$put(urlString = \"\${ktorClient.domain}$baseUrl${functionInfo.url}\")")
			}
			
			DELETE::class -> {
				codeBlock.beginControlFlow("$httpClient.$delete(urlString = \"\${ktorClient.domain}$baseUrl${functionInfo.url}\")")
			}
		}
		
		if (functionInfo.auth) {
			codeBlock.addStatement("$bearerAuth(ktorClient.getToken())")
		}
		
		val bodyList = mutableListOf<ValueParameterInfo>()
		val queryList = mutableListOf<ValueParameterInfo>()
		val formList = mutableListOf<ValueParameterInfo>()
		val headerList = mutableListOf<ValueParameterInfo>()
		valueParameterInfos.forEach {
			when (it.type) {
				Body::class -> bodyList += it
				Query::class -> queryList += it
				Form::class -> formList += it
				Header::class -> headerList += it
			}
		}
		if (bodyList.size > 1) {
			kspLogger.warn("@Body 只允许一个参数为Body")
		}
		if (bodyList.isNotEmpty()) {
			val bodyInfo = bodyList[0]
			codeBlock.addStatement("$contentType($ContentTypeClass.Application.Json)")
			codeBlock.addStatement("$setBody($TextContent($Json.$encodeToString(${bodyInfo.parameterName}), $ContentTypeClass.Application.Json))")
		}
		queryList.forEach {
			val parameterName = if (it.layer != null) {
				"${it.parameterName}.$sha256(layer = ${it.layer})"
			} else {
				it.parameterName
			}
			codeBlock.addStatement("$parameter(\"${it.name}\", $parameterName)")
		}
		if (formList.isNotEmpty()) {
			codeBlock.addStatement("$contentType($ContentTypeClass.MultiPart.FormData)")
				.beginControlFlow("val formData = $formData {")
			formList.forEach {
				val parameterName = if (it.layer != null) {
					"${it.parameterName}.$sha256(layer = ${it.layer})"
				} else {
					it.parameterName
				}
				codeBlock.addStatement("append(\"${it.name}\", $parameterName)")
			}
			codeBlock.endControlFlow()
				.addStatement("$setBody($MultiPartFormDataContent(formData))")
		}
		headerList.forEach {
			val parameterName = if (it.layer != null) {
				"${it.parameterName}.$sha256(layer = ${it.layer})"
			} else {
				it.parameterName
			}
			codeBlock.addStatement("header(\"${it.name}\", $parameterName)")
		}
		codeBlock.endControlFlow()
		when (returnQualifiedName) {
			ByteArray::class.qualifiedName -> {
				codeBlock.beginControlFlow("if (response.status.$isSuccess()) {")
					.addStatement("response.$readBytes()")
					.nextControlFlow("else")
					.addStatement("ByteArray(0)")
					.endControlFlow()
					.nextControlFlow("catch (e: Exception)")
					.addStatement("ByteArray(0)")
					.endControlFlow()
			}
			
			RESULT_BODY_QUALIFIED_NAME -> {
				codeBlock.beginControlFlow("if (response.status.$isSuccess()) {")
					.addStatement("response.$body()")
					.nextControlFlow("else")
					.addStatement("$ResultBody.failure(response.status.value, response.status.description)")
					.endControlFlow()
					.nextControlFlow("catch (e: Exception)")
					.addStatement("$ResultBody.exception(e)")
					.endControlFlow()
			}
			
			Unit::class.qualifiedName -> {
				codeBlock.nextControlFlow("finally")
					.endControlFlow()
			}
			
			else -> {
				kspLogger.warn(returnClassName.toString().split("<").first())
				throw IllegalStateException("不支持的类型")
			}
		}
		return codeBlock.build()
	}
	
	private fun KSFunctionDeclaration.getFunctionInfo(
		kClass: KClass<out Annotation>,
		urlProperty: KProperty1<out Annotation, String>,
		authProperty: KProperty1<out Annotation, Boolean>
	): FunctionInfo? {
		val annotation = this.getAnnotation(kClass) ?: return null
		val url = annotation.getArgumentValue(urlProperty)!!
		val auth = annotation.getArgumentValue(authProperty) ?: false
		return FunctionInfo(kClass, url, auth)
	}
	
	private fun KSValueParameter.getValueParameterInfo(
		kClass: KClass<out Annotation>,
		kProperty1: KProperty1<out Annotation, String>? = null
	): ValueParameterInfo? {
		val annotation = this.getAnnotation(kClass) ?: return null
		val parameterName = this.name!!.asString()
		val name = kProperty1?.let { annotation.getArgumentValue(it) ?: return null } ?: parameterName
		val layer = if (kClass in arrayOf(Header::class, Query::class, Form::class)) {
			val shA256 = this.getAnnotation(SHA256::class)
			if (shA256 != null) {
				shA256.getArgumentValue(SHA256::layer) ?: 1
			} else null
		} else null
		return ValueParameterInfo(kClass, this.type.resolve().declaration.className, name, parameterName, layer)
	}
	
	private data class FunctionInfo(
		val type: KClass<out Annotation>,
		val url: String,
		val auth: Boolean
	)
	
	private data class ValueParameterInfo(
		val type: KClass<out Annotation>,
		val className: ClassName,
		val name: String,
		val parameterName: String,
		val layer: Int?
	)
	
	override fun defaultHandler(
		node: KSNode,
		data: Unit
	): KSClassDeclaration = error("未使用")
}