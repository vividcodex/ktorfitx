package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.ReturnStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

internal class HttpClientCodeBlock(
	private val className: ClassName,
	private val returnStructure: ReturnStructure,
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		url: String,
		hasBuilder: Boolean,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, funName)
		val httpClientCode = "this.config.httpClient!!.$funName(\"$url\")"
		if (hasBuilder) {
			beginControlFlow(httpClientCode)
			builder()
			endControlFlow()
			if (returnStructure.rawType != ClassNames.Unit) {
				addStatement(returnFunName)
			}
		} else {
			addStatement(httpClientCode + returnFunName)
		}
	}
	
	private val returnFunName: String
		get() {
			var funName = when (returnStructure.notNullRawType) {
				ClassNames.Unit -> null
				ClassNames.ApiResult -> "safeApiResult"
				ClassNames.ByteArray -> "safeByteArray"
				ClassNames.String -> "safeText"
				else -> null
			}
			if (funName != null) {
				if (returnStructure.isNullable) {
					funName += "OrNull"
				}
				fileSpecBuilder.addImport(PackageNames.KTORFITX_CORE_EXPENDS, funName)
			}
			return funName?.let { ".$funName()" } ?: ""
		}
	
	override fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "bearerAuth")
		addStatement("this@${className.simpleName}.config.token?.invoke()?.let { bearerAuth(it) }")
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>,
	) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "headers")
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(\"$name\", \"$value\")")
		}
		headerModels.forEach {
			addStatement("append(\"${it.name}\", ${it.varName})")
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueriesCodeBlock(queryModels: List<QueryModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "parameter")
		queryModels.forEach {
			addStatement("parameter(\"${it.name}\", ${it.varName})")
		}
	}
	
	override fun CodeBlock.Builder.buildPartsCodeBlock(partModels: List<PartModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST_FORMS, "formData", "MultiPartFormDataContent")
		addStatement("contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("formData {")
		partModels.forEach {
			addStatement("append(\"${it.name}\", ${it.varName})")
		}
		nextControlFlow(".let")
		addStatement("setBody(MultiPartFormDataContent(it))")
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildFieldsCodeBlock(fieldModels: List<FieldModel>) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "formUrlEncode")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.FormUrlEncoded)")
		val code = fieldModels.joinToString {
			if (it.isString) {
				"\"${it.name}\" to ${it.varName}"
			} else {
				"\"${it.name}\" to ${it.varName}.toString()"
			}
		}
		addStatement("setBody(listOf($code).formUrlEncode())")
	}
	
	override fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel) {
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "contentType", "ContentType")
		fileSpecBuilder.addImport(PackageNames.KTOR_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(${bodyModel.varName})")
	}
}