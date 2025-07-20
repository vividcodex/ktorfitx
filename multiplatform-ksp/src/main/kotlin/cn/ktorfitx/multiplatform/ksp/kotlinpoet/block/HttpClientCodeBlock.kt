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
		funName: String,
		builder: CodeBlock.Builder.() -> Unit
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, funName)
		if (returnModel.returnKind == ReturnKind.Unit) {
			beginControlFlow("this.config.httpClient.%N", funName)
		} else {
			beginControlFlow("val response = this.config.httpClient.%N", funName)
		}
		builder()
		endControlFlow()
		
		val typeName = if (returnModel.returnKind == ReturnKind.Result) {
			(returnModel.typeName as ParameterizedTypeName).typeArguments.first()
		} else returnModel.typeName
		val rawType = if (typeName.isNullable) typeName.rawType.copy(nullable = false) else typeName
		if (returnModel.returnKind == ReturnKind.Unit) {
			return
		}
		if (returnModel.returnKind == ReturnKind.Result && rawType == TypeNames.Unit) {
			addStatement("Result.success(Unit)")
			return
		}
		
		val funName = when (rawType) {
			TypeNames.String -> "bodyAsText"
			TypeNames.ByteArray -> "bodyAsBytes"
			TypeNames.ByteReadChannel -> "bodyAsChannel"
			else -> "body"
		}
		if (funName == "body") {
			fileSpecBuilder.addImport(PackageNames.KTOR_CALL, funName)
		} else {
			fileSpecBuilder.addImport(PackageNames.KTOR_STATEMENT, funName)
		}
		if (returnModel.returnKind == ReturnKind.Result) {
			addStatement("Result.success(response.%N())", funName)
		} else if (returnModel.typeName != TypeNames.Unit) {
			addStatement("return response.%N()", funName)
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
	
	override fun CodeBlock.Builder.buildQueries(queryModels: List<QueryModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "parameter")
		queryModels.forEach {
			addStatement("this.parameter(%S, %N)", it.name, it.varName)
		}
	}
	
	override fun CodeBlock.Builder.buildParts(partModels: List<PartModel>) {
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
	
	override fun CodeBlock.Builder.buildFields(fieldModels: List<FieldModel>) {
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
	
	override fun CodeBlock.Builder.buildCookies(cookieModels: List<CookieModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "cookie")
		cookieModels.forEach { model ->
			val codeBlock = buildCodeBlock {
				add("this.cookie(\n")
				indent()
				add("name = %S,\n", model.name)
				add("value = %N,\n", model.varName)
				model.maxAge?.let { add("maxAge = %L,\n", it) }
				model.expires?.let {
					fileSpecBuilder.addImport(PackageNames.KTOR_UTIL_DATE, "GMTDate")
					add("expires = %L,\n", "GMTDate(${it}L)")
				}
				model.domain?.let { add("domain = %S,\n", it) }
				model.path?.let { add("path = %S,\n", it) }
				model.secure?.let { add("secure = %L,\n", it) }
				model.httpOnly?.let { add("httpOnly = %L,\n", it) }
				model.extensions?.let { add("extensions = %L,\n", it.toCodeBlock()) }
				unindent()
				add(")\n")
			}
			add(codeBlock)
		}
	}
	
	override fun CodeBlock.Builder.buildAttributes(cookieModels: List<AttributeModel>) {
		beginControlFlow("this.setAttributes")
		cookieModels.forEach {
			addStatement("this[%T(%S)] = %L", TypeNames.AttributeKey.parameterizedBy(it.typeName), it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBody(bodyModel: BodyModel) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("this.contentType(ContentType.Application.Json)")
		addStatement("this.setBody(%N)", bodyModel.varName)
	}
}