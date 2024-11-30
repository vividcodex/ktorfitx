package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.*
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ReturnStructure
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
		fullUrl: String,
		hasBuilder: Boolean,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, funName)
		val buildUrl = if (fullUrl.isHttpOrHttps()) fullUrl else "\${this.ktorfit.baseUrl}$fullUrl"
		val httpClientCode = "this.httpClient.$funName(\"$buildUrl\")"
		if (hasBuilder) {
			beginControlFlow(httpClientCode)
			builder()
			endControlFlow()
			if (returnStructure.rawType != ReturnTypes.unitClassName) {
				addStatement(returnFunName)
			}
		} else {
			addStatement(httpClientCode + returnFunName)
		}
	}
	
	private val returnFunName: String
		get() {
			var funName = when (returnStructure.notNullRawType) {
				ReturnTypes.unitClassName -> null
				ReturnTypes.resultBodyClassName -> "safeResultBody"
				ReturnTypes.byteArrayClassName -> "safeByteArray"
				ReturnTypes.stringClassName -> "safeText"
				else -> null
			}
			if (funName != null) {
				if (returnStructure.isNullable) {
					funName += "OrNull"
				}
				UseImports.addImports(KtorfitxQualifiers.PACKAGE_API_EXPENDS, funName)
			}
			return funName?.let { ".$funName()" } ?: ""
		}
	
	override fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "bearerAuth")
		addStatement("this@${className.simpleName}.ktorfit.token?.let { bearerAuth(it()) }")
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
	
	override fun CodeBlock.Builder.buildFormsCodeBlock(formModels: List<FormModel>) {
		UseImports.addImports(KtorQualifiers.PACKAGE_HTTP, "contentType", "ContentType")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "setBody")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST_FORMS, "formData", "MultiPartFormDataContent")
		addStatement("contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("formData {")
		formModels.forEach {
			addStatement("append(\"${it.name}\", ${it.varName})")
		}
		nextControlFlow(".let")
		addStatement("setBody(MultiPartFormDataContent(it))")
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel) {
		UseImports.addImports(KtorQualifiers.PACKAGE_HTTP, "contentType", "ContentType")
		UseImports.addImports(KtorQualifiers.PACKAGE_REQUEST, "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(${bodyModel.varName})")
	}
}