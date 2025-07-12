package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import com.squareup.kotlinpoet.CodeBlock

/**
 * MockClient 代码块
 */
internal class MockClientCodeBlock(
	private val mockModel: MockModel
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		val topLevelClassName = mockModel.provider.topLevelClassName()
		fileSpecBuilder.addImport(topLevelClassName.packageName, topLevelClassName.simpleName)
		fileSpecBuilder.addImport(PackageNames.KTORFITX_MOCK_CONFIG, "mockClient")
		beginControlFlow(
			"""
			val value = this.config.mockClient.%N(
				mockProvider = %N,
				delay = %L
			)
			""".trimIndent(),
			funName,
			mockModel.provider.simpleNames.joinToString("."),
			mockModel.delay
		)
		builder()
		endControlFlow()
		addStatement("Result.success(value)")
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