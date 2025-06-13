package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.check.compileCheck
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ReturnClassNames
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.UseImports
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.*
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfitx.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import kotlin.reflect.KClass

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/14 17:24
 *
 * 文件介绍：CodeBlockBuilder
 */
internal class HttpCodeBlockBuilder(
	private val classStructure: ClassStructure,
	private val funStructure: FunStructure,
	private val codeBlockKClass: KClass<out ClientCodeBlock>,
) {
	
	private val returnStructure = funStructure.returnStructure
	private val valueParameterModels = funStructure.valueParameterModels
	private val functionModels = funStructure.functionModels
	private val apiStructure = classStructure.apiStructure
	
	private companion object Companion {
		private val exceptionClassNames = arrayOf(
			ClassName.bestGuess("kotlin.Exception"),
			ClassName.bestGuess("java.lang.Exception"),
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
		beginControlFlow(if (returnStructure.rawType != ReturnClassNames.unit) "return try" else "try")
		builder()
		val exceptionListenerModels = functionModels.filterIsInstance<ExceptionListenerModel>()
		exceptionListenerModels.forEach {
			UseImports += it.exceptionTypeName
			UseImports += it.listenerClassName
			nextControlFlow("catch (e: ${it.exceptionTypeName.simpleName})")
			beginControlFlow("with(${it.listenerClassName.simpleName})")
			val superinterfaceName = classStructure.superinterface.simpleName
			val funName = funStructure.funName
			addStatement("$superinterfaceName::$funName.onExceptionListener(e)")
			endControlFlow()
			if (it.returnTypeName == ReturnClassNames.unit) {
				buildExceptionReturnCodeBlock()
			}
		}
		if (exceptionListenerModels.all { it.exceptionTypeName !in exceptionClassNames }) {
			if (returnStructure.rawType == ReturnClassNames.resultBody) {
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
			ReturnClassNames.resultBody -> {
				addStatement("ResultBody.exception(e)")
			}
			
			ReturnClassNames.byteArray -> {
				addStatement("ByteArray(0)")
			}
			
			ReturnClassNames.string -> {
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
			acc.replace("{${it.name}}", "\${${it.varName}}")
		}
		return fullUrl
	}
	
	private fun isNeedClientBuilder(): Boolean {
		return valueParameterModels.any { it !is PathModel } || functionModels.any { it is BearerAuthModel || it is HeadersModel }
	}
}