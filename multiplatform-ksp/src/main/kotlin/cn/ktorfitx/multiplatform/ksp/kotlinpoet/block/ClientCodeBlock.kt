package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.multiplatform.ksp.model.model.*
import com.squareup.kotlinpoet.CodeBlock

internal sealed interface ClientCodeBlock {
	
	/**
	 * httpClient or mockClient
	 */
	fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		url: String,
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
	 * parts
	 */
	fun CodeBlock.Builder.buildPartsCodeBlock(partModels: List<PartModel>)
	
	/**
	 * fields
	 */
	fun CodeBlock.Builder.buildFieldsCodeBlock(fieldModels: List<FieldModel>)
	
	/**
	 * body
	 */
	fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel)
}