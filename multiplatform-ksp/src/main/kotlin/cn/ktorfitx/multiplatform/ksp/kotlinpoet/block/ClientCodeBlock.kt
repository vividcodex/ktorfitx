package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.multiplatform.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock

internal sealed interface ClientCodeBlock {
	
	/**
	 * httpClient or mockClient
	 */
	fun CodeBlock.Builder.buildClientCodeBlock(
		httpRequestModel: HttpRequestModel,
		isPrepareType: Boolean,
		builder: CodeBlock.Builder.() -> Unit
	)
	
	/**
	 * StaticUrl
	 */
	fun CodeBlock.Builder.buildStaticUrlString(
		urlString: String
	)
	
	/**
	 * DynamicUrl
	 */
	fun CodeBlock.Builder.buildDynamicUrlString(
		dynamicUrl: DynamicUrl,
		apiUrl: String?,
		pathModels: List<PathModel>
	)
	
	fun CodeBlock.Builder.buildTimeoutCodeBlock(
		timeoutModel: TimeoutModel
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
	fun CodeBlock.Builder.buildQueries(
		queryModels: List<QueryModel>
	)
	
	/**
	 * parts
	 */
	fun CodeBlock.Builder.buildParts(
		partModels: List<PartModel>
	)
	
	/**
	 * fields
	 */
	fun CodeBlock.Builder.buildFields(
		fieldModels: List<FieldModel>
	)
	
	/**
	 * cookies
	 */
	fun CodeBlock.Builder.buildCookies(
		cookieModels: List<CookieModel>
	)
	
	/**
	 * cookies
	 */
	fun CodeBlock.Builder.buildAttributes(
		cookieModels: List<AttributeModel>
	)
	
	/**
	 * body
	 */
	fun CodeBlock.Builder.buildBody(
		bodyModel: BodyModel
	)
}