package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.expends.rawType
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.AnyReturnStructure
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName

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
			val name = "\"${model.name}\""
			val value = ", ${model.varName}"
			val maxAge = model.maxAge?.let { ", maxAge = $it" } ?: ""
			val expires = model.expires?.let {
				fileSpecBuilder.addImport(PackageNames.KTOR_UTIL_DATE, "GMTDate")
				", expires = GMTDate(${it}L)"
			} ?: ""
			val domain = model.domain?.let { ", domain = \"$it\"" } ?: ""
			val path = model.path?.let { ", path = \"$it\"" } ?: ""
			val secure = model.secure?.let { ", secure = $it" } ?: ""
			val httpOnly = model.httpOnly?.let { ", httpOnly = $it" } ?: ""
			val extensions = model.extensions?.let {
				", extensions = mapOf(${it.map { entry -> "\"${entry.key}\" to \"${entry.value}\"" }.joinToString()})"
			} ?: ""
			addStatement("cookie($name$value$maxAge$expires$domain$path$secure$httpOnly$extensions)")
		}
	}
	
	override fun CodeBlock.Builder.buildBody(bodyModel: BodyModel) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(%N)", bodyModel.varName)
	}
}