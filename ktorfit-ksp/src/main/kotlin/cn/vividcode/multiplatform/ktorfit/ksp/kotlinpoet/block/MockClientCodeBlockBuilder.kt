package cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfit.ksp.expends.classNames
import cn.vividcode.multiplatform.ktorfit.ksp.model.EncryptInfo
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.*
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/4 23:07
 *
 * 文件介绍：MockClientCodeBlockBuilder
 */
internal class MockClientCodeBlockBuilder(
	private val classStructure: ClassStructure,
	funStructure: FunStructure,
	addImport: (String, Array<out String>) -> Unit,
) : CodeBlockBuilder(funStructure, addImport) {
	
	private val functionModels = funStructure.functionModels
	
	private val valueParameterModels = funStructure.valueParameterModels
	
	private val returnStructure = funStructure.returnStructure
	
	/**
	 * 构建 MockCodeBlock
	 */
	override fun CodeBlock.Builder.buildCodeBlock() {
		addImports(returnStructure.typeName.classNames)
		buildMockRequestCodeBlock {
			buildBearerAuthCodeBlock()
			buildHeadersCodeBlock()
			buildQueriesFormsPathsCodeBlock("queries", QueryModel::name, QueryModel::encryptInfo)
			buildQueriesFormsPathsCodeBlock("forms", FormModel::name, FormModel::encryptInfo)
			buildQueriesFormsPathsCodeBlock("paths", PathModel::name, PathModel::encryptInfo)
			buildBodyCodeBlock()
		}
	}
	
	private fun CodeBlock.Builder.buildMockRequestCodeBlock(
		block: CodeBlock.Builder.() -> Unit
	) {
		val apiModel = functionModels.first { it is ApiModel } as ApiModel
		val mockModel = functionModels.first { it is MockModel } as MockModel
		addImport(mockModel.provider)
		addImport(mockModel.status.packageName, mockModel.status.simpleNames.first())
		val url = classStructure.apiStructure.url + formatUrl(apiModel.url)
		val fullUrl = "\"\${this.ktorfit.baseUrl}$url\""
		val provider = mockModel.provider.simpleName
		val status = "MockStatus.${mockModel.status.simpleName}"
		val delayRange = mockModel.delayRange.let { "${it[0]}L..${if (it.size == 1) "${it[0]}L" else "${it[1]}L"}" }
		val params = "$fullUrl, $provider, $status, $delayRange"
		beginControlFlow("return this.mockClient.${apiModel.requestFunName}($params)")
		block()
		endControlFlow()
	}
	
	private fun formatUrl(url: String): String {
		return url
	}
	
	private fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		val bearerAuth = functionModels.filterIsInstance<BearerAuthModel>().isNotEmpty()
		if (bearerAuth) {
			addStatement("ktorfit.token?.let { bearerAuth(it()) }")
		}
	}
	
	/**
	 * 构建 headers
	 */
	private fun CodeBlock.Builder.buildHeadersCodeBlock() {
		val headersModel = functionModels.filterIsInstance<HeadersModel>().firstOrNull()
		val headerModels = valueParameterModels.filterIsInstance<HeaderModel>()
		if (headersModel == null && headerModels.isEmpty()) return
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(\"$name\", \"$value\")")
		}
		headerModels.forEach {
			val varName = encrypt(it.varName, it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	/**
	 * 构建 queries, forms, paths
	 */
	private inline fun <reified T : ValueParameterModel> CodeBlock.Builder.buildQueriesFormsPathsCodeBlock(
		funName: String,
		noinline name: T.() -> String,
		noinline encryptInfo: T.() -> EncryptInfo?
	) {
		val models = valueParameterModels.filterIsInstance<T>()
		if (models.isEmpty()) return
		beginControlFlow(funName)
		models.forEach {
			val varName = encrypt(it.varName, it.encryptInfo())
			addStatement("append(\"${it.name()}\", $varName)")
		}
		endControlFlow()
	}
	
	/**
	 * 构建 body
	 */
	private fun CodeBlock.Builder.buildBodyCodeBlock() {
		val bodyModel = valueParameterModels.find { it is BodyModel } as? BodyModel ?: return
		addStatement("body(${bodyModel.varName})")
	}
	
	/**
	 * encrypt
	 */
	private fun encrypt(varName: String, encryptInfo: EncryptInfo?): String {
		if (encryptInfo == null) return varName
		addImport("cn.vividcode.multiplatform.ktorfit.api.encrypt", "encrypt", "EncryptType")
		return "encrypt($varName, EncryptType.${encryptInfo.encryptType}, ${encryptInfo.layer})"
	}
}