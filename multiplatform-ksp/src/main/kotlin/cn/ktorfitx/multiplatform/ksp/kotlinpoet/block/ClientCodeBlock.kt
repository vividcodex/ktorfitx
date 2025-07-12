package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.multiplatform.ksp.model.model.*
import com.squareup.kotlinpoet.CodeBlock

internal sealed interface ClientCodeBlock {
	
	/**
	 * httpClient or mockClient
	 */
	fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		builder: CodeBlock.Builder.() -> Unit
	)
	
	/**
	 * urlString
	 */
	fun CodeBlock.Builder.buildUrlString(
		urlString: String
	)
	
	/**
	 * bearerAuth
	 */
	fun CodeBlock.Builder.buildBearerAuth(
		varName: String
	)
	
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
	fun CodeBlock.Builder.buildQueries(queryModels: List<QueryModel>)
	
	/**
	 * parts
	 */
	fun CodeBlock.Builder.buildParts(partModels: List<PartModel>)
	
	/**
	 * fields
	 */
	fun CodeBlock.Builder.buildFields(fieldModels: List<FieldModel>)
	
	/**
	 * body
	 */
	fun CodeBlock.Builder.buildBody(bodyModel: BodyModel)
}