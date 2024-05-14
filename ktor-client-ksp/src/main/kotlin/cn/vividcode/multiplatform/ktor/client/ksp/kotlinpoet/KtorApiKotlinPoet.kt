package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet

import cn.vividcode.multiplatform.ktor.client.api.model.ResultBody
import cn.vividcode.multiplatform.ktor.client.ksp.model.ClassModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.FunctionModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.ParameterModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.PathModel
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
		private val ktorConfigClassName = ClassName("cn.vividcode.multiplatform.ktor.client.api.config", "KtorConfig")
		private val httpClientClassName = ClassName("io.ktor.client", "HttpClient")
		
		private val importMap = mapOf(
			"cn.vividcode.multiplatform.ktor.client.api.expends" to arrayOf("sha256"),
			"cn.vividcode.multiplatform.ktor.client.api.model" to arrayOf("ResultBody"),
			"kotlinx.serialization" to arrayOf("encodeToString"),
			"kotlinx.serialization.json" to arrayOf("Json"),
			"io.ktor.http" to arrayOf("contentType", "ContentType", "isSuccess"),
			"io.ktor.http.content" to arrayOf("TextContent"),
			"io.ktor.client.call" to arrayOf("body"),
			"io.ktor.client.request" to arrayOf("get", "post", "put", "delete", "head", "options", "patch", "bearerAuth", "setBody", "parameter", "header"),
			"io.ktor.client.request.forms" to arrayOf("formData", "MultiPartFormDataContent"),
			"io.ktor.client.statement" to arrayOf("readBytes")
		)
		
		private val simpleNameMap = importMap.flatMap { (packageName, simpleNames) ->
			simpleNames.map { it to packageName }
		}.toMap()
	}
	
	private val useImports by lazy { mutableMapOf<String, String>() }
	
	/**
	 * 生成文件
	 */
	fun getFileSpec(classModel: ClassModel): FileSpec {
		val fileSpecBuilder = FileSpec.builder(classModel.className)
			.indent("\t")
			.addType(getTypeSpec(classModel))
			.addProperty(getApiPropertySpec(classModel.className, classModel.superinterface))
		useImports.forEach { simpleName, packageName ->
			fileSpecBuilder.addImport(packageName, simpleName)
		}
		useImports.clear()
		return fileSpecBuilder.build()
	}
	
	/**
	 * 生成实现类
	 */
	private fun getTypeSpec(classModel: ClassModel): TypeSpec {
		val primaryConstructor = FunSpec.constructorBuilder()
			.addModifiers(KModifier.PRIVATE)
			.addParameter("ktorConfig", ktorConfigClassName)
			.addParameter("httpClient", httpClientClassName)
			.build()
		val ktorConfigPropertySpec = PropertySpec.builder("ktorConfig", ktorConfigClassName)
			.addModifiers(KModifier.PRIVATE)
			.mutable(false)
			.initializer("ktorConfig")
			.build()
		val httpClientPropertySpec = PropertySpec.builder("httpClient", httpClientClassName)
			.addModifiers(KModifier.PRIVATE)
			.mutable(false)
			.initializer("httpClient")
			.build()
		return TypeSpec.classBuilder(classModel.className)
			.addModifiers(KModifier.PUBLIC)
			.addSuperinterface(classModel.superinterface)
			.primaryConstructor(primaryConstructor)
			.addProperty(ktorConfigPropertySpec)
			.addProperty(httpClientPropertySpec)
			.addType(companionObjectBuilder(classModel.className, classModel.superinterface))
			.addFunctions(getFunSpecs(classModel.functionModels))
			.build()
	}
	
	/**
	 * 生成扩展属性
	 */
	private fun getApiPropertySpec(className: ClassName, superinterface: ClassName): PropertySpec {
		val getter = FunSpec.getterBuilder()
			.addStatement("return ${className.simpleName}.getInstance(this.ktorConfig, this.httpClient)", superinterface)
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
			.beginControlFlow("return instance ?: ${className.simpleName}(ktorConfig, httpClient).also")
			.addStatement("instance = it")
			.endControlFlow()
			.build()
		val functionSpec = FunSpec.builder("getInstance")
			.addModifiers(KModifier.PUBLIC)
			.returns(superinterface)
			.addParameter("ktorConfig", ktorConfigClassName)
			.addParameter("httpClient", httpClientClassName)
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
		val httpClient = (if (isReturn) "val response = " else "") + "this.httpClient"
		
		val requestTypeName = use(functionModel.requestType.simpleName!!.lowercase())
		val url = parsePathToUrl(functionModel.url, functionModel.pathModels)
		beginControlFlow("$httpClient.${requestTypeName}(urlString = \"\${ktorConfig.domain}$url\")")
		if (functionModel.auth) {
			use("bearerAuth")
			addStatement("bearerAuth(ktorConfig.getToken!!())")
		}
		if (functionModel.bodyModel != null) {
			use("contentType")
			use("ContentType")
			use("setBody")
			use("TextContent")
			use("Json")
			use("encodeToString")
			addStatement("contentType(ContentType.Application.Json)")
			addStatement("setBody(TextContent(Json.encodeToString(${functionModel.bodyModel.variableName}), ContentType.Application.Json))")
		}
		functionModel.queryModels.forEach {
			val variableName = if (it.sha256Layer > 0) {
				use("sha256")
				"${it.variableName}.sha256(layer = ${it.sha256Layer})"
			} else {
				it.variableName
			}
			use("parameter")
			addStatement("parameter(\"${it.name}\", $variableName)")
		}
		if (functionModel.formModels.isNotEmpty()) {
			use("contentType")
			use("ContentType")
			use("formData")
			use("setBody")
			use("MultiPartFormDataContent")
			addStatement("contentType(ContentType.MultiPart.FormData)")
			beginControlFlow("val formData = formData {")
			functionModel.formModels.forEach {
				val variableName = if (it.sha256Layer > 0) {
					use("sha256")
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
			use("header")
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
			use("header")
			functionModel.headersModels.forEach {
				addStatement("header(\"${it.name}\", \"${it.value}\")")
			}
		}
		endControlFlow()
		val returnQualifiedName = functionModel.returnTypeName.toString().split("<").first()
		when (returnQualifiedName) {
			ByteArray::class.qualifiedName -> {
				use("isSuccess")
				use("readBytes")
				beginControlFlow("if (response.status.isSuccess())")
				addStatement("response.readBytes()")
				nextControlFlow("else")
				addStatement("ByteArray(0)")
				endControlFlow()
				nextControlFlow("catch (e: Exception)")
				addStatement("ByteArray(0)")
			}
			
			ResultBody::class.qualifiedName -> {
				use("isSuccess")
				use("body")
				use("ResultBody")
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
	 * 解析 Path
	 */
	private fun parsePathToUrl(rawUrl: String, pathModels: List<PathModel>): String {
		var url = rawUrl
		if (pathModels.isNotEmpty()) {
			use("sha256")
			pathModels.forEach {
				val newValue = if (it.sha256Layer == 0) {
					"\${${it.variableName}}"
				} else {
					"\${${it.variableName}.sha256(layer = ${it.sha256Layer})}"
				}
				url = url.replace("{${it.name}}", newValue)
			}
		}
		return url
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
	
	private fun use(simpleName: String): String {
		useImports[simpleName] = simpleNameMap[simpleName]!!
		return simpleName
	}
}