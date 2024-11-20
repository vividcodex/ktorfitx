package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.*
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ReturnStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
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
		UseImports.addImports("io.ktor.client.request", funName)
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
				UseImports.addImports("cn.vividcode.multiplatform.ktorfitx.api.expends", funName)
			}
			return funName?.let { ".$funName()" } ?: ""
		}
	
	override fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		UseImports.addImports("io.ktor.client.request", "bearerAuth")
		addStatement("this@${className.simpleName}.ktorfit.token?.let { bearerAuth(it()) }")
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>,
	) {
		UseImports.addImports("io.ktor.client.request", "headers")
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(\"$name\", \"$value\")")
		}
		headerModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueriesCodeBlock(queryModels: List<QueryModel>) {
		UseImports.addImports("io.ktor.client.request", "parameter")
		queryModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("parameter(\"${it.name}\", $varName)")
		}
	}
	
	override fun CodeBlock.Builder.buildFormsCodeBlock(formModels: List<FormModel>) {
		UseImports.addImports("io.ktor.http", "contentType", "ContentType")
		UseImports.addImports("io.ktor.client.request", "setBody")
		UseImports.addImports("io.ktor.client.request.forms", "formData", "MultiPartFormDataContent")
		addStatement("contentType(ContentType.MultiPart.FormData)")
		beginControlFlow("formData {")
		formModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		nextControlFlow(".let")
		addStatement("setBody(MultiPartFormDataContent(it))")
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel) {
		UseImports.addImports("io.ktor.http", "contentType", "ContentType")
		UseImports.addImports("io.ktor.client.request", "setBody")
		addStatement("contentType(ContentType.Application.Json)")
		addStatement("setBody(${bodyModel.varName})")
	}
}