package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.AnyReturnStructure
import com.squareup.kotlinpoet.CodeBlock

/**
 * MockClient 代码块
 */
internal class MockClientCodeBlock(
	private val mockModel: MockModel,
	private val returnStructure: AnyReturnStructure
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		fileSpecBuilder.addImport(PackageNames.KTORFITX_MOCK_CONFIG, "mockClient")
		when {
			returnStructure.isUnit -> {
				beginControlFlow(
					"""
					this.config.mockClient.%N(
						mockProvider = %T,
						delay = %L
					)
					""".trimIndent(),
					funName,
					mockModel.provider,
					mockModel.delay
				)
			}
			
			returnStructure.isResult -> {
				beginControlFlow(
					"""
					val result = this.config.mockClient.%N(
						mockProvider = %T,
						delay = %L
					)
					""".trimIndent(),
					funName,
					mockModel.provider,
					mockModel.delay
				)
			}
			
			else -> {
				beginControlFlow(
					"""
					return this.config.mockClient.%N(
						mockProvider = %T,
						delay = %L
					)
					""".trimIndent(),
					funName,
					mockModel.provider,
					mockModel.delay
				)
			}
		}
		builder()
		endControlFlow()
		when {
			returnStructure.isResult -> {
				addStatement("Result.success(result)")
			}
		}
	}
	
	override fun CodeBlock.Builder.buildUrlString(urlString: String) {
		addStatement("url(\"$urlString\")")
	}
	
	override fun CodeBlock.Builder.buildBearerAuth(
		varName: String
	) {
		addStatement("%N?.let { bearerAuth(it) }", varName)
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>,
	) {
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(%S, %S)", name, value)
		}
		headerModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueries(queryModels: List<QueryModel>) {
		beginControlFlow("queries")
		queryModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildParts(partModels: List<PartModel>) {
		beginControlFlow("parts")
		partModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildFields(fieldModels: List<FieldModel>) {
		beginControlFlow("fields")
		fieldModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildCookies(cookieModels: List<CookieModel>) {
		beginControlFlow("cookies")
		fileSpecBuilder.addImport(PackageNames.KTORFITX_MOCK, "MockClient")
		cookieModels.forEach {
			addStatement(
				"append(%S, MockCookie(%S, %N, %L, %L, %L, %L, %L, %L, %L))",
				it.name,
				it.varName,
				it.maxAge,
				it.expires,
				it.domain,
				it.path,
				it.secure,
				it.httpOnly,
				it.extensions
			)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildAttributes(cookieModels: List<AttributeModel>) {
		beginControlFlow("attributes")
		cookieModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	fun CodeBlock.Builder.buildPaths(pathModels: List<PathModel>) {
		beginControlFlow("paths")
		pathModels.forEach {
			addStatement("append(%S, %N)", it.name, it.varName)
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBody(bodyModel: BodyModel) {
		addStatement("body(%N)", bodyModel.varName)
	}
}