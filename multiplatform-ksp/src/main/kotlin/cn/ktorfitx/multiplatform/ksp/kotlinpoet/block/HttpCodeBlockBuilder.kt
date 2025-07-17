package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.isHttpOrHttps
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.ReturnKind
import com.squareup.kotlinpoet.CodeBlock
import kotlin.reflect.KClass

internal class HttpCodeBlockBuilder(
	classStructure: ClassStructure,
	private val funStructure: FunStructure,
	private val codeBlockKClass: KClass<out ClientCodeBlock>,
) {
	
	private val returnStructure = funStructure.returnStructure
	private val valueParameterModels = funStructure.valueParameterModels
	private val funModels = funStructure.funModels
	private val apiStructure = classStructure.apiStructure
	
	fun CodeBlock.Builder.buildCodeBlock(
		tokenVarName: String?
	) {
		buildTryCatchIfNeed {
			with(getClientCodeBlock()) {
				val apiModel = funModels.first { it is ApiModel } as ApiModel
				val funName = apiModel.requestFunName
				buildClientCodeBlock(funName) {
					val fullUrl = parseToFullUrl(apiModel.url)
					buildUrlString(fullUrl)
					if (tokenVarName != null) {
						buildBearerAuth(tokenVarName)
					}
					val headersModel = funModels.find { it is HeadersModel } as? HeadersModel
					val headerModels = valueParameterModels.filterIsInstance<HeaderModel>()
					if (headerModels.isNotEmpty() || headersModel != null) {
						buildHeadersCodeBlock(headersModel, headerModels)
					}
					val queryModels = valueParameterModels.filterIsInstance<QueryModel>()
					if (queryModels.isNotEmpty()) {
						buildQueries(queryModels)
					}
					val partModels = valueParameterModels.filterIsInstance<PartModel>()
					if (partModels.isNotEmpty()) {
						buildParts(partModels)
					}
					val fieldModels = valueParameterModels.filterIsInstance<FieldModel>()
					if (fieldModels.isNotEmpty()) {
						buildFields(fieldModels)
					}
					val cookieModels = valueParameterModels.filterIsInstance<CookieModel>()
					if (cookieModels.isNotEmpty()) {
						buildCookies(cookieModels)
					}
					val attributeModels = valueParameterModels.filterIsInstance<AttributeModel>()
					if (attributeModels.isNotEmpty()) {
						buildAttributes(attributeModels)
					}
					if (this@with is MockClientCodeBlock) {
						val pathModels = valueParameterModels.filterIsInstance<PathModel>()
						if (pathModels.isNotEmpty()) {
							buildPaths(pathModels)
						}
					}
					val bodyModel = valueParameterModels.filterIsInstance<BodyModel>().firstOrNull()
					if (bodyModel != null) {
						buildBody(bodyModel)
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.buildTryCatchIfNeed(
		builder: CodeBlock.Builder.() -> Unit
	) {
		if (returnStructure.returnKind == ReturnKind.Result) {
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
		return when (this.codeBlockKClass) {
			HttpClientCodeBlock::class -> {
				HttpClientCodeBlock(returnStructure)
			}
			
			MockClientCodeBlock::class -> {
				val mockModel = funStructure.funModels.first { it is MockModel } as MockModel
				MockClientCodeBlock(mockModel, returnStructure)
			}
			
			else -> error("不支持的类型")
		}
	}
	
	private fun parseToFullUrl(url: String): String {
		val pathModels = valueParameterModels.filterIsInstance<PathModel>()
		val initialUrl = if (url.isHttpOrHttps()) url else {
			val apiUrl = apiStructure.url
			if (url.isHttpOrHttps()) return url
			if (apiUrl == null) return url
			"$apiUrl/$url"
		}
		val fullUrl = pathModels.fold(initialUrl) { acc, it ->
			it.parameter.compileCheck(url.contains("{${it.name}}")) {
				val funName = funStructure.funName
				"$funName 函数上的 ${it.varName} 参数上的 @Path 注解的 name 参数没有在 url 上找到"
			}
			acc.replace("{${it.name}}", $$"${$${it.varName}}")
		}
		return fullUrl
	}
}