package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.multiplatform.ksp.constants.KtorQualifiers
import cn.ktorfitx.multiplatform.ksp.constants.KtorfitxQualifiers
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.ReturnClassNames
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.UseImports
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.ReturnStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/14 17:31
 *
 * 文件介绍：HttpClientCodeBlock
 */
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
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, funName)
		val httpClientCode = "this.httpClient.$funName(\"$url\")"
		if (hasBuilder) {
			beginControlFlow(httpClientCode)
			builder()
			endControlFlow()
			if (returnStructure.rawType != ReturnClassNames.unit) {
				addStatement(returnFunName)
			}
		} else {
			addStatement(httpClientCode + returnFunName)
		}
	}
	
	private val returnFunName: String
		get() {
			var funName = when (returnStructure.notNullRawType) {
				ReturnClassNames.unit -> null
				ReturnClassNames.apiResult -> "safeApiResult"
				ReturnClassNames.byteArray -> "safeByteArray"
				ReturnClassNames.string -> "safeText"
				else -> null
			}
			if (funName != null) {
				if (returnStructure.isNullable) {
					funName += "OrNull"
				}
				UseImports.addImports(KtorfitxQualifiers.PACKAGE_CORE_EXPENDS, funName)
			}
			return funName?.let { ".$funName()" } ?: ""
		}
	
	override fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "bearerAuth")
		addStatement("this@${className.simpleName}.ktorfit.token?.invoke()?.let { bearerAuth(it) }")
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>,
	) {
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "headers")
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
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "parameter")
		queryModels.forEach {
			addStatement("parameter(\"${it.name}\", ${it.varName})")
		}
	}
	
	override fun CodeBlock.Builder.buildPartsCodeBlock(partModels: List<PartModel>) {
		UseImports.addImports(KtorQualifiers.PACKAGE_HTTP, "contentType", "ContentType")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "setBody")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST_FORMS, "formData", "MultiPartFormDataContent")
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
		UseImports.addImports(KtorQualifiers.PACKAGE_HTTP, "contentType", "ContentType")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "setBody")
		UseImports.addImports(KtorQualifiers.PACKAGE_HTTP, "formUrlEncode")
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
		UseImports.addImports(KtorQualifiers.PACKAGE_HTTP, "contentType", "ContentType")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(${bodyModel.varName})")
	}
}