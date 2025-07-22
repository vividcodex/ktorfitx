package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.toCodeBlock
import cn.ktorfitx.common.ksp.util.expends.replaceFirstToUppercase
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.*
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock

/**
 * MockClient 代码块
 */
internal class MockClientCodeBlock(
	private val mockModel: MockModel,
	private val returnModel: ReturnModel
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		httpRequestModel: HttpRequestModel,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		fileSpecBuilder.addImport(PackageNames.KTORFITX_MOCK_CONFIG, "mockClient")
		fileSpecBuilder.addImport(PackageNames.KTOR_HTTP, "HttpMethod")
		when (returnModel.returnKind) {
			ReturnKind.Unit -> addStatement("this.config.mockClient.request(")
			ReturnKind.Result -> addStatement("val result = this.config.mockClient.request(")
			ReturnKind.Any -> {
				add("return ")
				addStatement("this.config.mockClient.request(")
			}
		}
		indent()
		if (httpRequestModel.isCustom) {
			addStatement("method = HttpMethod(%S),", httpRequestModel.method)
		} else {
			addStatement("method = HttpMethod.%N,", httpRequestModel.method.lowercase().replaceFirstToUppercase())
		}
		addStatement("mockProvider = %T,", mockModel.provider)
		if (mockModel.delay > 0L) {
			addStatement("delay = %LL", mockModel.delay)
		}
		unindent()
		beginControlFlow(")")
		builder()
		endControlFlow()
		if (returnModel.returnKind == ReturnKind.Result) {
			addStatement("Result.success(result)")
		}
	}
	
	override fun CodeBlock.Builder.buildUrlString(urlString: String) {
		addStatement("this.url(\"$urlString\")")
	}
	
	override fun CodeBlock.Builder.buildTimeoutCodeBlock(timeoutModel: TimeoutModel) {
		beginControlFlow("this.timeout")
		if (timeoutModel.requestTimeoutMillis != null) {
			addStatement("this.requestTimeoutMillis = %LL", timeoutModel.requestTimeoutMillis)
		}
		if (timeoutModel.connectTimeoutMillis != null) {
			addStatement("this.connectTimeoutMillis = %LL", timeoutModel.connectTimeoutMillis)
		}
		if (timeoutModel.socketTimeoutMillis != null) {
			addStatement("this.socketTimeoutMillis = %LL", timeoutModel.socketTimeoutMillis)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBearerAuth(
		varName: String
	) {
		beginControlFlow("if (%N != null)", varName)
		addStatement("this.bearerAuth(%N)", varName)
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>,
	) {
		beginControlFlow("this.headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("this.append(%S, %S)", name, value)
		}
		headerModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueries(queryModels: List<QueryModel>) {
		beginControlFlow("this.queries")
		queryModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildParts(partModels: List<PartModel>) {
		beginControlFlow("this.parts")
		partModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildFields(fieldModels: List<FieldModel>) {
		beginControlFlow("this.fields")
		fieldModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildCookies(cookieModels: List<CookieModel>) {
		beginControlFlow("this.cookies")
		fileSpecBuilder.addImport(PackageNames.KTORFITX_MOCK, "MockClient")
		cookieModels.forEach { model ->
			val codeBlock = buildCodeBlock {
				addStatement("this.append(")
				indent()
				addStatement("name = %S,", model.name)
				addStatement("value = %N,", model.varName)
				model.maxAge?.let { addStatement("maxAge = %L,", it) }
				model.expires?.let {
					fileSpecBuilder.addImport(PackageNames.KTOR_UTIL_DATE, "GMTDate")
					addStatement("expires = GMTDate(%LL),", it)
				}
				model.domain?.let { addStatement("domain = %S,", it) }
				model.path?.let { addStatement("path = %S,", it) }
				model.secure?.let { addStatement("secure = %L,", it) }
				model.httpOnly?.let { addStatement("httpOnly = %L,", it) }
				model.extensions?.let { addStatement("extensions = %L,", it.toCodeBlock()) }
				unindent()
				add(")\n")
			}
			add(codeBlock)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildAttributes(cookieModels: List<AttributeModel>) {
		beginControlFlow("this.attributes")
		cookieModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	fun CodeBlock.Builder.buildPaths(pathModels: List<PathModel>) {
		beginControlFlow("this.paths")
		pathModels.forEach {
			addStatement("this.append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBody(bodyModel: BodyModel) {
		addStatement("this.body(%N)", bodyModel.varName)
	}
}