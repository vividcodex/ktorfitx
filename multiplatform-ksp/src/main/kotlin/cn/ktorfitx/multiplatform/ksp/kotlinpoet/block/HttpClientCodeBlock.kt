package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.toCodeBlock
import cn.ktorfitx.common.ksp.util.expends.rawType
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.buildCodeBlock

internal class HttpClientCodeBlock(
	private val returnModel: ReturnModel
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		httpRequestModel: HttpRequestModel,
		isPrepareType: Boolean,
		builder: CodeBlock.Builder.() -> Unit
	) {
		val isCustom = httpRequestModel.isCustom
		val funName = when {
			isCustom && isPrepareType -> "prepareRequest"
			isCustom && !isPrepareType -> "request"
			isPrepareType -> "prepare${httpRequestModel.method.lowercase().replaceFirstChar { it.uppercase() }}"
			else -> httpRequestModel.method.lowercase()
		}
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, funName)
		
		if (returnModel.returnKind == ReturnKind.Unit) {
			beginControlFlow("this.config.httpClient.%N", funName)
			customHttpMethodCodeBlock(httpRequestModel, builder)
			endControlFlow()
			return
		}
		
		var typeName = returnModel.typeName
		if (returnModel.returnKind == ReturnKind.Result) {
			typeName = (typeName as ParameterizedTypeName).typeArguments.first()
		}
		val rawType = if (typeName.isNullable) typeName.rawType.copy(nullable = false) else typeName
		val bodyFunName = when (rawType) {
			TypeNames.String -> "bodyAsText"
			TypeNames.ByteArray -> "bodyAsBytes"
			TypeNames.ByteReadChannel -> "bodyAsChannel"
			else -> "body"
		}
		fileSpecBuilder.addImport(if (bodyFunName == "body") PackageNames.KTOR_CALL else PackageNames.KTOR_STATEMENT, bodyFunName)
		if (returnModel.returnKind == ReturnKind.Any) {
			if (isPrepareType) {
				add("return ")
				beginControlFlow("this.config.httpClient.%N {", funName)
			} else {
				beginControlFlow("val response = this.config.httpClient.%N {", funName)
			}
			customHttpMethodCodeBlock(httpRequestModel, builder)
			endControlFlow()
			if (!isPrepareType) {
				addStatement("return response.%N()", bodyFunName)
			}
			return
		}
		
		beginControlFlow("val response = this.config.httpClient.%N", funName)
		customHttpMethodCodeBlock(httpRequestModel, builder)
		endControlFlow()
		addStatement("Result.success(response.%N())", bodyFunName)
	}
	
	private fun CodeBlock.Builder.customHttpMethodCodeBlock(
		httpRequestModel: HttpRequestModel,
		builder: CodeBlock.Builder.() -> Unit
	) {
		if (httpRequestModel.isCustom) {
			fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "HttpMethod")
			addStatement("this.method = HttpMethod(%S)", httpRequestModel.method)
		}
		builder()
		if (httpRequestModel.isCustom) {
			addStatement("this.method = HttpMethod(%S)", httpRequestModel.method)
		}
	}
	
	override fun CodeBlock.Builder.buildUrlString(
		urlString: String
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "url")
		addStatement("this.url(\"$urlString\")")
	}
	
	override fun CodeBlock.Builder.buildTimeoutCodeBlock(
		timeoutModel: TimeoutModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_PLUGINS, "timeout")
		beginControlFlow("this.timeout")
		if (timeoutModel.requestTimeoutMillis != null) {
			addStatement("this.requestTimeoutMillis = %LL", timeoutModel.requestTimeoutMillis)
		}
		if (timeoutModel.connectTimeoutMillis != null) {
			addStatement("this.connectTimeoutMillis = %LL", timeoutModel.connectTimeoutMillis)
		}
		if (timeoutModel.socketTimeoutMillis != null) {
			addStatement("this.socketTimeoutMillis = %LL", timeoutModel.socketTimeoutMillis)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBearerAuth(
		varName: String
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "bearerAuth")
		beginControlFlow("if (%N != null)", varName)
		addStatement("this.bearerAuth(%N)", varName)
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "headers")
		beginControlFlow("this.headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("this.append(%S, %S)", name, value)
		}
		headerModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueries(
		queryModels: List<QueryModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "parameter")
		queryModels.forEach {
			addStatement("this.parameter(%S, %N)", it.name, it.varName)
		}
	}
	
	override fun CodeBlock.Builder.buildParts(
		partModels: List<PartModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST_FORMS, "formData", "MultiPartFormDataContent")
		addStatement("this.contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("formData {")
		partModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		nextControlFlow(".let")
		addStatement("this.setBody(MultiPartFormDataContent(it))")
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildFields(
		fieldModels: List<FieldModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType", "formUrlEncode")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("this.contentType(ContentType.Application.FormUrlEncoded)")
		val parameters = fieldModels.joinToString { "%S to %L" }
		val args = fieldModels.flatMap {
			when {
				it.isStringType -> listOf(it.name, it.varName)
				it.isNullable -> listOf(it.name, "${it.varName}?.toString()")
				else -> listOf(it.name, "${it.varName}.toString()")
			}
		}
		addStatement("this.setBody(listOf($parameters).formUrlEncode())", *args.toTypedArray())
	}
	
	override fun CodeBlock.Builder.buildCookies(
		cookieModels: List<CookieModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "cookie")
		cookieModels.forEach { model ->
			val codeBlock = buildCodeBlock {
				addStatement("this.cookie(")
				indent()
				addStatement("name = %S,", model.name)
				addStatement("value = %N,", model.varName)
				model.maxAge?.let { addStatement("maxAge = %L,", it) }
				model.expires?.let {
					fileSpecBuilder.addImport(PackageNames.KTOR_UTIL_DATE, "GMTDate")
					addStatement("expires = GMTDate(%LL),", it)
				}
				model.domain?.let { addStatement("domain = %S,", it) }
				model.path?.let { addStatement("path = %S,", it) }
				model.secure?.let { addStatement("secure = %L,", it) }
				model.httpOnly?.let { addStatement("httpOnly = %L,", it) }
				model.extensions?.let { addStatement("extensions = %L", it.toCodeBlock()) }
				unindent()
				addStatement(")")
			}
			add(codeBlock)
		}
	}
	
	override fun CodeBlock.Builder.buildAttributes(
		cookieModels: List<AttributeModel>
	) {
		beginControlFlow("this.setAttributes")
		cookieModels.forEach {
			addStatement("this[%T(%S)] = %L", TypeNames.AttributeKey.parameterizedBy(it.typeName), it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBody(
		bodyModel: BodyModel
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		val format = when (bodyModel.formatClassName) {
			TypeNames.SerializationFormatJson -> "Json"
			TypeNames.SerializationFormatXml -> "Xml"
			TypeNames.SerializationFormatCbor -> "Cbor"
			TypeNames.SerializationFormatProtoBuf -> "ProtoBuf"
			else -> error("不支持的类型 ${bodyModel.formatClassName.simpleName}")
		}
		addStatement("this.contentType(ContentType.Application.%N)", format)
		addStatement("this.setBody(%N)", bodyModel.varName)
	}
}