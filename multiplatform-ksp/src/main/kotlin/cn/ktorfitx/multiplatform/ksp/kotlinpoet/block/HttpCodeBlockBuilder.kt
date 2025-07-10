package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.allClassNames
import cn.ktorfitx.common.ksp.util.expends.isHttpOrHttps
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.AnyReturnStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName
import kotlin.reflect.KClass

internal class HttpCodeBlockBuilder(
	private val classStructure: ClassStructure,
	private val funStructure: FunStructure,
	private val codeBlockKClass: KClass<out ClientCodeBlock>,
) {
	
	private val returnStructure = funStructure.returnStructure as AnyReturnStructure
	private val valueParameterModels = funStructure.valueParameterModels
	private val functionModels = funStructure.functionModels
	private val apiStructure = classStructure.apiStructure
	
	fun CodeBlock.Builder.buildCodeBlock() {
		buildTryCatchIfNeed {
			with(getClientCodeBlock()) {
				val apiModel = functionModels.first { it is ApiModel } as ApiModel
				val funName = apiModel.requestFunName
				buildClientCodeBlock(funName) {
					val fullUrl = parseToFullUrl(apiModel.url)
					buildUrlString(fullUrl)
					val bearerAuth = functionModels.any { it is BearerAuthModel }
					if (bearerAuth) {
						buildBearerAuth()
					}
					val headersModel = functionModels.find { it is HeadersModel } as? HeadersModel
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
					if (this@with is MockClientCodeBlock) {
						val pathModels = valueParameterModels.filterIsInstance<PathModel>()
						if (pathModels.isNotEmpty()) {
							buildPaths(pathModels)
						}
					}
					val bodyModel = valueParameterModels.filterIsInstance<BodyModel>().firstOrNull()
					if (bodyModel != null) {
						val typeName = bodyModel.typeName
						when (typeName) {
							is ClassName -> {
								val topLevelClassName = typeName.topLevelClassName()
								fileSpecBuilder.addImport(topLevelClassName.packageName, topLevelClassName.simpleNames)
							}
							
							is ParameterizedTypeName -> {
								typeName.allClassNames.forEach {
									fileSpecBuilder.addImport(it.packageName, it.simpleName)
								}
							}
							
							else -> null
						}
						buildBody(bodyModel)
					}
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.buildTryCatchIfNeed(
		builder: CodeBlock.Builder.() -> Unit
	) {
		if (returnStructure.isResult) {
			beginControlFlow("return try")
			builder()
			nextControlFlow("catch (e: %T)", ClassNames.CancellationException)
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
				HttpClientCodeBlock(classStructure.className, returnStructure)
			}
			
			MockClientCodeBlock::class -> {
				val mockModel = funStructure.functionModels.first { it is MockModel } as MockModel
				MockClientCodeBlock(classStructure.className, mockModel)
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
			it.valueParameter.compileCheck(url.contains("{${it.name}}")) {
				val funName = funStructure.funName
				"$funName 方法上的 ${it.varName} 参数上的 @Path 注解的 name 参数没有在 url 上找到"
			}
			acc.replace("{${it.name}}", $$"${$${it.varName}}")
		}
		return fullUrl
	}
}