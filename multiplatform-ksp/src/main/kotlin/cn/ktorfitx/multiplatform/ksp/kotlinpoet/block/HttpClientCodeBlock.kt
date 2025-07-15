package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.toCodeBlock
import cn.ktorfitx.common.ksp.util.expends.rawType
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.AnyReturnStructure
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.buildCodeBlock

internal class HttpClientCodeBlock(
	private val returnStructure: AnyReturnStructure
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		builder: CodeBlock.Builder.() -> Unit
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, funName)
		if (returnStructure.isUnit) {
			beginControlFlow("this.config.httpClient!!.%N", funName)
		} else {
			beginControlFlow("val response = this.config.httpClient!!.%N", funName)
		}
		builder()
		endControlFlow()
		
		val typeName = if (returnStructure.isResult) {
			(returnStructure.typeName as ParameterizedTypeName).typeArguments.first()
		} else returnStructure.typeName
		val rawType = if (typeName.isNullable) typeName.rawType.copy(nullable = false) else typeName
		val funName = when (rawType) {
			ClassNames.String -> "bodyAsText"
			ClassNames.ByteArray -> "bodyAsBytes"
			ClassNames.ByteReadChannel -> "bodyAsChannel"
			else -> "body"
		}
		if (funName == "body") {
			fileSpecBuilder.addImport(PackageNames.KTOR_CALL, funName)
		} else {
			fileSpecBuilder.addImport(PackageNames.KTOR_STATEMENT, funName)
		}
		if (returnStructure.isResult) {
			addStatement("Result.success(response.%N())", funName)
		} else if (returnStructure.typeName != ClassNames.Unit) {
			addStatement("return response.%N()", funName)
		}
	}
	
	override fun CodeBlock.Builder.buildUrlString(
		urlString: String
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "url")
		addStatement("url(\"$urlString\")")
	}
	
	override fun CodeBlock.Builder.buildBearerAuth(
		varName: String
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "bearerAuth")
		addStatement("%N?.let { bearerAuth(it) }", varName)
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "headers")
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(%S, %S)", name, value)
		}
		headerModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueries(queryModels: List<QueryModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "parameter")
		queryModels.forEach {
			addStatement("parameter(%S, %N)", it.name, it.varName)
		}
	}
	
	override fun CodeBlock.Builder.buildParts(partModels: List<PartModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST_FORMS, "formData", "MultiPartFormDataContent")
		addStatement("contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("formData {")
		partModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		nextControlFlow(".let")
		addStatement("setBody(MultiPartFormDataContent(it))")
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildFields(fieldModels: List<FieldModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "formUrlEncode")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.FormUrlEncoded)")
		val body = fieldModels.joinToString {
			"\"${it.varName}\" to ${it.varName}${if (it.isStringType) "" else ".toString()"}"
		}
		addStatement("setBody(listOf($body).formUrlEncode())")
	}
	
	override fun CodeBlock.Builder.buildCookies(cookieModels: List<CookieModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "cookie")
		cookieModels.forEach { model ->
			val codeBlock = buildCodeBlock {
				add("cookie(\n")
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
	
	override fun CodeBlock.Builder.buildBody(bodyModel: BodyModel) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(%N)", bodyModel.varName)
	}
}