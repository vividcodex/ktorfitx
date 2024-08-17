package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnTypes
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.*
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
	private val returnRawType: ClassName,
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		fullUrl: String,
		isNeedClientBuilder: Boolean,
		builder: CodeBlock.Builder.() -> Unit
	) {
		UseImports.addImports("io.ktor.client.request", funName)
		if (returnRawType == ReturnTypes.unitClassName) {
			if (isNeedClientBuilder) {
				beginControlFlow("this.httpClient.$funName(\"\${this.ktorfit.baseUrl}$fullUrl\")")
				builder()
				endControlFlow()
			} else {
				addStatement("this.httpClient.$funName(\"\${this.ktorfit.baseUrl}$fullUrl\")")
			}
		} else {
			if (isNeedClientBuilder) {
				beginControlFlow("this.httpClient.$funName(\"\${this.ktorfit.baseUrl}$fullUrl\")")
				builder()
				nextControlFlow(".let")
			} else {
				beginControlFlow("this.httpClient.$funName(\"\${this.ktorfit.baseUrl}$fullUrl\").let")
			}
			UseImports.addImports("io.ktor.http", "isSuccess")
			beginControlFlow("if (it.status.isSuccess())")
			if (returnRawType == ReturnTypes.byteArrayClassName) {
				UseImports.addImports("io.ktor.client.statement", "readBytes")
				addStatement("it.readBytes()")
			} else {
				UseImports.addImports("io.ktor.client.call", "body")
				addStatement("it.body()")
			}
			nextControlFlow("else")
			if (returnRawType.isNullable) {
				addStatement("null")
			} else if (returnRawType == ReturnTypes.byteArrayClassName) {
				addStatement("ByteArray(0)")
			} else {
				UseImports += ReturnTypes.resultBodyClassName
				addStatement("ResultBody.failure(it.status.value, it.status.description)")
			}
			endControlFlow()
			endControlFlow()
		}
	}
	
	override fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		UseImports.addImports("io.ktor.client.request", "bearerAuth")
		addStatement("ktorfit.token?.let { bearerAuth(it()) }")
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>
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