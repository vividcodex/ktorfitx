package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.isHttpOrHttps
import cn.ktorfitx.common.ksp.util.expends.simpleName
import cn.ktorfitx.common.ksp.util.imports.UseImports
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock
import kotlin.reflect.KClass

internal class HttpCodeBlockBuilder(
	private val classStructure: ClassStructure,
	private val funStructure: FunStructure,
	private val codeBlockKClass: KClass<out ClientCodeBlock>,
) {
	
	private val returnStructure = funStructure.returnStructure
	private val valueParameterModels = funStructure.valueParameterModels
	private val functionModels = funStructure.functionModels
	private val apiStructure = classStructure.apiStructure
	
	private companion object {
		
		private val exceptionClassNames = arrayOf(
			ClassNames.KotlinException,
			ClassNames.JavaException,
		)
	}
	
	fun CodeBlock.Builder.buildCodeBlock() = with(getClientCodeBlock()) {
		buildExceptionCodeBlock {
			val apiModel = functionModels.first { it is ApiModel } as ApiModel
			val funName = apiModel.requestFunName
			val fullUrl = parseToFullUrl(apiModel.url)
			val isNeedClientBuilder = isNeedClientBuilder()
			buildClientCodeBlock(funName, fullUrl, isNeedClientBuilder) {
				val bearerAuth = functionModels.any { it is BearerAuthModel }
				if (bearerAuth) {
					buildBearerAuthCodeBlock()
				}
				val headersModel = functionModels.find { it is HeadersModel } as? HeadersModel
				val headerModels = valueParameterModels.filterIsInstance<HeaderModel>()
				if (headerModels.isNotEmpty() || headersModel != null) {
					buildHeadersCodeBlock(headersModel, headerModels)
				}
				val queryModels = valueParameterModels.filterIsInstance<QueryModel>()
				if (queryModels.isNotEmpty()) {
					buildQueriesCodeBlock(queryModels)
				}
				val partModels = valueParameterModels.filterIsInstance<PartModel>()
				if (partModels.isNotEmpty()) {
					buildPartsCodeBlock(partModels)
				}
				val fieldModels = valueParameterModels.filterIsInstance<FieldModel>()
				if (fieldModels.isNotEmpty()) {
					buildFieldsCodeBlock(fieldModels)
				}
				if (this@with is MockClientCodeBlock) {
					val pathModels = valueParameterModels.filterIsInstance<PathModel>()
					if (pathModels.isNotEmpty()) {
						buildPathsCodeBlock(pathModels)
					}
				}
				val bodyModel = valueParameterModels.filterIsInstance<BodyModel>().firstOrNull()
				if (bodyModel != null) {
					UseImports += bodyModel.typeQualifiedName
					buildBodyCodeBlock(bodyModel)
				}
			}
		}
	}
	
	private fun CodeBlock.Builder.buildExceptionCodeBlock(
		builder: CodeBlock.Builder.() -> Unit,
	) {
		beginControlFlow(if (returnStructure.rawType != ClassNames.Unit) "return try" else "try")
		builder()
		val exceptionListenerModels = functionModels.filterIsInstance<ExceptionListenerModel>()
		exceptionListenerModels.forEach {
			UseImports += it.exceptionTypeName
			UseImports += it.listenerClassName
			nextControlFlow("catch (e: ${it.exceptionTypeName.simpleName})")
			val simpleNames = it.listenerClassName.simpleNames.joinToString(".")
			beginControlFlow("with($simpleNames)")
			val superinterfaceName = classStructure.superinterface.simpleName
			val funName = funStructure.funName
			addStatement("$superinterfaceName::$funName.onExceptionListener(e)")
			endControlFlow()
			if (it.returnTypeName == ClassNames.Unit) {
				buildExceptionReturnCodeBlock()
			}
		}
		if (exceptionListenerModels.all { it.exceptionTypeName !in exceptionClassNames }) {
			if (returnStructure.rawType == ClassNames.ApiResult) {
				nextControlFlow("catch (e: Exception)")
			} else {
				nextControlFlow("catch (_: Exception)")
			}
			buildExceptionReturnCodeBlock()
		}
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildExceptionReturnCodeBlock() {
		if (returnStructure.isNullable) {
			addStatement("null")
			return
		}
		when (returnStructure.rawType) {
			ClassNames.ApiResult -> {
				addStatement("ApiResult.exception(e)")
			}
			
			ClassNames.ByteArray -> {
				addStatement("ByteArray(0)")
			}
			
			ClassNames.String -> {
				addStatement("\"\"")
			}
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
	
	private fun isNeedClientBuilder(): Boolean {
		return valueParameterModels.any { it !is PathModel } || functionModels.any { it is BearerAuthModel || it is HeadersModel }
	}
}