package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.*
import com.squareup.kotlinpoet.CodeBlock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/14 17:30
 *
 * 文件介绍：ClientCodeBlock
 */
internal sealed interface ClientCodeBlock {
	
	/**
	 * httpClient or mockClient
	 */
	fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		fullUrl: String,
		hasBuilder: Boolean,
		builder: CodeBlock.Builder.() -> Unit
	)
	
	/**
	 * bearerAuth
	 */
	fun CodeBlock.Builder.buildBearerAuthCodeBlock()
	
	/**
	 * headers
	 */
	fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>
	)
	
	/**
	 * queries
	 */
	fun CodeBlock.Builder.buildQueriesCodeBlock(queryModels: List<QueryModel>)
	
	/**
	 * forms
	 */
	fun CodeBlock.Builder.buildFormsCodeBlock(formModels: List<FormModel>)
	
	/**
	 * body
	 */
	fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel)
}