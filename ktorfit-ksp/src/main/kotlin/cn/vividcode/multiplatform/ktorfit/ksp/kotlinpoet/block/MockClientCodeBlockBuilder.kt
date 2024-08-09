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
	
	private val varNameUrl = getVarName("url")
	
	private val varNameMockName = getVarName("mockName")
	
	/**
	 * 构建 MockCodeBlock
	 */
	override fun CodeBlock.Builder.buildCodeBlock() {
		addImports(returnStructure.typeName.classNames)
		val apiModel = functionModels.first { it is ApiModel } as ApiModel
		val mockModel = functionModels.first { it is MockModel } as MockModel
		val funName = apiModel.requestFunName
		addImport("cn.vividcode.multiplatform.ktorfit.api.mock", funName)
		addStatement("val $varNameUrl = \"${classStructure.apiStructure.url}${apiModel.url}\"")
		addStatement("val $varNameMockName = \"${mockModel.name}\"")
		beginControlFlow("return this.mockClient.$funName($varNameUrl, $varNameMockName)")
		
		buildBearerAuthCodeBlock()
		buildHeadersCodeBlock()
		buildQueriesFormsPathsCodeBlock("queries", QueryModel::name, QueryModel::encryptInfo)
		buildQueriesFormsPathsCodeBlock("forms", FormModel::name, FormModel::encryptInfo)
		buildQueriesFormsPathsCodeBlock("paths", PathModel::name, PathModel::encryptInfo)
		buildBodyCodeBlock()
		
		endControlFlow()
	}
	
	private fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		val apiModel = functionModels.filterIsInstance<ApiModel>().first()
		if (apiModel.auth) {
			addImport("cn.vividcode.multiplatform.ktorfit.api.mock", "bearerAuth")
			addStatement("bearerAuth(ktorConfig.token!!())")
		}
	}
	
	/**
	 * 构建 headers
	 */
	private fun CodeBlock.Builder.buildHeadersCodeBlock() {
		val headersModel = functionModels.filterIsInstance<HeadersModel>().firstOrNull()
		val headerModels = valueParameterModels.filterIsInstance<HeaderModel>()
		if (headersModel == null && headerModels.isEmpty()) return
		addImport("cn.vividcode.multiplatform.ktorfit.api.mock", "headers", "append")
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(\"$name\", \"$value\")")
		}
		headerModels.forEach {
			val varName = it.varName + encrypt(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	/**
	 * 构建 queries, forms, paths
	 */
	private inline fun <reified T : ValueParameterModel> CodeBlock.Builder.buildQueriesFormsPathsCodeBlock(
		funName: String,
		noinline getName: T.() -> String,
		noinline getEncryptInfo: T.() -> EncryptInfo?
	) {
		val models = valueParameterModels.filterIsInstance<T>()
		if (models.isEmpty()) return
		addImport("cn.vividcode.multiplatform.ktorfit.api.mock", funName, "append")
		beginControlFlow(funName)
		models.forEach {
			val varName = it.varName + encrypt(it.getEncryptInfo())
			addStatement("append(\"${it.getName()}\", $varName)")
		}
		endControlFlow()
	}
	
	/**
	 * 构建 body
	 */
	private fun CodeBlock.Builder.buildBodyCodeBlock() {
		val bodyModel = valueParameterModels.find { it is BodyModel } as? BodyModel ?: return
		addImport("cn.vividcode.multiplatform.ktorfit.api.mock", "body")
		addStatement("body(${bodyModel.varName})")
	}
	
	/**
	 * encrypt
	 */
	private fun encrypt(encryptInfo: EncryptInfo?): String {
		if (encryptInfo == null) return ""
		addImport("cn.vividcode.multiplatform.ktorfit.api.encrypt", "encrypt", "EncryptType")
		return ".encrypt(EncryptType.${encryptInfo.encryptType}, ${encryptInfo.layer})"
	}
}