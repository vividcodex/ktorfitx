package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.expends.isHttpOrHttps
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock

internal class HttpCodeBlockBuilder(
	private val classModel: ClassModel,
	private val funModel: FunModel,
	private val httpRequestModel: HttpRequestModel,
	private val tokenVarName: String?
) {
	
	fun CodeBlock.Builder.buildCodeBlock() {
		buildTryCatchIfNeed {
			with(getClientCodeBlock()) {
				val url = httpRequestModel.url
				val funName = httpRequestModel.className.simpleName.lowercase()
				buildClientCodeBlock(funName) {
					val fullUrl = parseToFullUrl(url)
					buildUrlString(fullUrl)
					funModel.timeoutModel?.let { buildTimeoutCodeBlock(it) }
					tokenVarName?.let { buildBearerAuth(it) }
					val headersModel = funModel.headersModel
					val headerModels = funModel.headerModels
					if (headersModel != null || headerModels.isNotEmpty()) {
						buildHeadersCodeBlock(headersModel, headerModels)
					}
					val queryModels = funModel.queryModels
					if (queryModels.isNotEmpty()) {
						buildQueries(queryModels)
					}
					val cookieModels = funModel.cookieModels
					if (cookieModels.isNotEmpty()) {
						buildCookies(cookieModels)
					}
					val attributeModels = funModel.attributeModels
					if (attributeModels.isNotEmpty()) {
						buildAttributes(attributeModels)
					}
					if (this@with is MockClientCodeBlock) {
						val pathModels = funModel.pathModels
						if (pathModels.isNotEmpty()) {
							buildPaths(pathModels)
						}
					}
					val requestBodyModel = funModel.requestBodyModel
					when (funModel.requestBodyModel) {
						is BodyModel -> buildBody(requestBodyModel)
						is FieldModels -> buildFields(requestBodyModel.fieldModels)
						is PartModels -> buildParts(requestBodyModel.partModels)
						null -> {}
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.buildTryCatchIfNeed(
		builder: CodeBlock.Builder.() -> Unit
	) {
		if (funModel.returnModel.returnKind == ReturnKind.Result) {
			beginControlFlow("return try")
			builder()
			nextControlFlow("catch (e: %T)", TypeNames.CancellationException)
			addStatement("throw e")
			nextControlFlow("catch (e: Exception)")
			addStatement("Result.failure(e)")
			endControlFlow()
		} else {
			builder()
		}
	}
	
	private fun getClientCodeBlock(): ClientCodeBlock {
		val mockModel = funModel.mockModel
		return if (mockModel != null) {
			MockClientCodeBlock(mockModel, funModel.returnModel)
		} else {
			HttpClientCodeBlock(funModel.returnModel)
		}
	}
	
	private fun parseToFullUrl(url: String): String {
		val pathModels = funModel.pathModels
		val initialUrl = if (url.isHttpOrHttps()) url else {
			val apiUrl = classModel.apiUrl
			if (apiUrl == null || url.isHttpOrHttps()) return url
			"$apiUrl/$url"
		}
		val fullUrl = pathModels.fold(initialUrl) { acc, it ->
			acc.replace("{${it.name}}", $$"${$${it.varName}}")
		}
		return fullUrl
	}
}