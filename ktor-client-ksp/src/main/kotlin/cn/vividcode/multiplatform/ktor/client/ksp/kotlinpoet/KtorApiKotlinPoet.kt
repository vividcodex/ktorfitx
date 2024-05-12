package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet

import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.model.ClassModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.FunctionModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.ParameterModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.RequestType
import com.squareup.kotlinpoet.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/12 下午5:49
 *
 * 介绍：KtorApiKotlinPoet
 */
internal class KtorApiKotlinPoet {
	
	companion object {
		private val ktorClientClassName = ClassName("cn.vividcode.multiplatform.ktor.client.api", "KtorClient")
	}
	
	private val useImports by lazy { mutableMapOf<String, MutableSet<String>>() }
	
	/**
	 * 生成文件
	 */
	fun getFileSpec(classModel: ClassModel): FileSpec {
		val fileSpecBuilder = FileSpec.builder(classModel.className)
			.indent("\t")
			.addType(getTypeSpec(classModel))
			.addProperty(getApiPropertySpec(classModel.className, classModel.superinterface))
		useImports.forEach(fileSpecBuilder::addImport)
		useImports.clear()
		return fileSpecBuilder.build()
	}
	
	/**
	 * 生成实现类
	 */
	private fun getTypeSpec(classModel: ClassModel): TypeSpec {
		val primaryConstructor = FunSpec.constructorBuilder()
			.addParameter("ktorClient", ktorClientClassName)
			.build()
		val propertySpec = PropertySpec.builder("ktorClient", ktorClientClassName)
			.addModifiers(KModifier.PRIVATE)
			.mutable(false)
			.initializer("ktorClient")
			.build()
		return TypeSpec.classBuilder(classModel.className)
			.addModifiers(KModifier.PUBLIC)
			.addSuperinterface(classModel.superinterface)
			.primaryConstructor(primaryConstructor)
			.addProperty(propertySpec)
			.addType(companionObjectBuilder(classModel.className, classModel.superinterface))
			.addFunctions(getFunSpecs(classModel.functionModels))
			.build()
	}
	
	/**
	 * 生成扩展属性
	 */
	private fun getApiPropertySpec(className: ClassName, superinterface: ClassName): PropertySpec {
		val getter = FunSpec.getterBuilder()
			.addStatement("return ${className.simpleName}.getInstance(this)", superinterface)
			.build()
		val name = superinterface.simpleName.replaceFirstChar { it.lowercase() }
		return PropertySpec.builder(name, superinterface)
			.receiver(ktorClientClassName)
			.getter(getter)
			.build()
	}
	
	/**
	 * 生成构造方法
	 */
	private fun companionObjectBuilder(className: ClassName, superinterface: ClassName): TypeSpec {
		val propertySpec = PropertySpec.builder("instance", superinterface.copy(nullable = true))
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
			.returns(superinterface)
			.addParameter("ktorClient", ktorClientClassName)
			.addCode(codeBlock)
			.build()
		return TypeSpec.companionObjectBuilder()
			.addProperty(propertySpec)
			.addFunction(functionSpec)
			.build()
	}
	
	/**
	 * 生成实现方法
	 */
	private fun getFunSpecs(functionModels: List<FunctionModel>): List<FunSpec> {
		return functionModels.map {
			FunSpec.builder(it.functionName)
				.addModifiers(KModifier.SUSPEND, KModifier.OVERRIDE)
				.addParameters(getParameterSpecs(it.parameterModels))
				.returns(it.returnTypeName)
				.addCode(getCodeBlock(it))
				.build()
		}
	}
	
	/**
	 * 生成代码
	 */
	private fun getCodeBlock(functionModel: FunctionModel): CodeBlock = buildCodeBlock {
		val isReturn = functionModel.returnTypeName != Unit::class.asTypeName()
		beginControlFlow("${if (isReturn) "return " else ""}try {")
		val httpClient = (if (isReturn) "val response = " else "") + "ktorClient.httpClient"
		val requestTypeName = when (functionModel.requestTypeModel.type) {
			RequestType.GET -> use("io.ktor.client.request", "get")
			RequestType.POST -> use("io.ktor.client.request", "post")
			RequestType.PUT -> use("io.ktor.client.request", "put")
			RequestType.DELETE -> use("io.ktor.client.request", "delete")
		}
		beginControlFlow("$httpClient.${requestTypeName}(urlString = \"\${ktorClient.domain}${functionModel.requestTypeModel.url}\")")
		if (functionModel.requestTypeModel.auth) {
			use("io.ktor.client.request", "bearerAuth")
			addStatement("bearerAuth(ktorClient.getToken())")
		}
		if (functionModel.bodyModel != null) {
			use("io.ktor.http", "contentType")
			use("io.ktor.http", "ContentType")
			use("io.ktor.client.request", "setBody")
			use("io.ktor.http.content", "TextContent")
			use("kotlinx.serialization.json", "Json")
			use("kotlinx.serialization", "encodeToString")
			addStatement("contentType(ContentType.Application.Json)")
			addStatement("setBody(TextContent(Json.encodeToString(${functionModel.bodyModel.variableName}), ContentType.Application.Json))")
		}
		functionModel.queryModels.forEach {
			val variableName = if (it.sha256Layer > 0) {
				use("cn.vividcode.multiplatform.ktor.client.api.expends", "sha256")
				"${it.variableName}.sha256(layer = ${it.sha256Layer})"
			} else {
				it.variableName
			}
			use("io.ktor.client.request", "parameter")
			addStatement("parameter(\"${it.name}\", $variableName)")
		}
		if (functionModel.formModels.isNotEmpty()) {
			use("io.ktor.http", "contentType")
			use("io.ktor.http", "ContentType")
			use("io.ktor.client.request.forms", "formData")
			use("io.ktor.client.request", "setBody")
			use("io.ktor.client.request.forms", "MultiPartFormDataContent")
			addStatement("contentType(ContentType.MultiPart.FormData)")
			beginControlFlow("val formData = formData {")
			functionModel.formModels.forEach {
				val variableName = if (it.sha256Layer > 0) {
					use("cn.vividcode.multiplatform.ktor.client.api.expends", "sha256")
					"${it.variableName}.sha256(layer = ${it.sha256Layer})"
				} else {
					it.variableName
				}
				addStatement("append(\"${it.name}\", $variableName)")
			}
			endControlFlow()
			addStatement("setBody(MultiPartFormDataContent(formData))")
		}
		if (functionModel.headerModels.isNotEmpty()) {
			use("io.ktor.client.request", "header")
			functionModel.headerModels.forEach {
				val variableName = if (it.sha256Layer > 0) {
					"${it.variableName}.sha256(layer = ${it.sha256Layer})"
				} else {
					it.variableName
				}
				addStatement("header(\"${it.name}\", $variableName)")
			}
		}
		if (functionModel.headersModels.isNotEmpty()) {
			use("io.ktor.client.request", "header")
			functionModel.headersModels.forEach {
				addStatement("header(\"${it.name}\", \"${it.value}\")")
			}
		}
		endControlFlow()
		val returnQualifiedName = functionModel.returnTypeName.toString().split("<").first()
		when (returnQualifiedName) {
			ByteArray::class.qualifiedName -> {
				use("io.ktor.http", "isSuccess")
				use("io.ktor.client.statement", "readBytes")
				beginControlFlow("if (response.status.isSuccess())")
				addStatement("response.readBytes()")
				nextControlFlow("else")
				addStatement("ByteArray(0)")
				endControlFlow()
				nextControlFlow("catch (e: Exception)")
				addStatement("ByteArray(0)")
			}
			
			ResultBody::class.qualifiedName -> {
				use("io.ktor.http", "isSuccess")
				use("io.ktor.client.call", "body")
				use("cn.vividcode.multiplatform.ktor.client.api.model", "ResultBody")
				beginControlFlow("if (response.status.isSuccess())")
				addStatement("response.body()")
				nextControlFlow("else")
				addStatement("ResultBody.failure(response.status.value, response.status.description)")
				endControlFlow()
				nextControlFlow("catch (e: Exception)")
				addStatement("ResultBody.exception(e)")
			}
			
			Unit::class.qualifiedName -> {
				nextControlFlow("finally")
			}
		}
		endControlFlow()
	}
	
	/**
	 * 生成参数
	 */
	private fun getParameterSpecs(parameterModels: List<ParameterModel>): List<ParameterSpec> {
		return parameterModels.map {
			ParameterSpec.builder(it.name, it.className)
				.build()
		}
	}
	
	private fun use(packageName: String, simpleName: String): String {
		val simpleNames = useImports.getOrPut(packageName) { mutableSetOf() }
		simpleNames += simpleName
		return simpleName
	}
}