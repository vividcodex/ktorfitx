package cn.ktorfitx.multiplatform.ksp.kotlinpoet.block

import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.toCodeBlock
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.ReturnKind
import cn.ktorfitx.multiplatform.ksp.model.structure.ReturnStructure
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock

/**
 * MockClient 代码块
 */
internal class MockClientCodeBlock(
	private val mockModel: MockModel,
	private val returnStructure: ReturnStructure
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		fileSpecBuilder.addImport(PackageNames.KTORFITX_MOCK_CONFIG, "mockClient")
		when (returnStructure.returnKind) {
			ReturnKind.Unit -> {
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
			
			ReturnKind.Result -> {
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
			
			ReturnKind.Any -> {
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
		if (returnStructure.returnKind == ReturnKind.Result) {
			addStatement("Result.success(result)")
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
		cookieModels.forEach { model ->
//			addStatement(
//				"append(%S, %N, %L, %L, %S, %S, %L, %L, %L)",
//				it.name,
//				it.varName,
//				it.maxAge,
//				it.expires,
//				it.domain,
//				it.path,
//				it.secure,
//				it.httpOnly,
//				it.extensions?.toCodeBlock()
//			)
			
			val codeBlock = buildCodeBlock {
				add("append(\n")
				indent()
				add("name = %S,\n", model.name)
				add("value = %N,\n", model.varName)
				model.maxAge?.let { add("maxAge = %L,\n", it) }
				model.expires?.let {
					fileSpecBuilder.addImport(PackageNames.KTOR_UTIL_DATE, "GMTDate")
					add("expires = %L,\n", "GMTDate(${it}L)")
				}
				model.domain?.let { add("domain = %S,\n", it) }
				model.path?.let { add("path = %S,\n", it) }
				model.secure?.let { add("secure = %L,\n", it) }
				model.httpOnly?.let { add("httpOnly = %L,\n", it) }
				model.extensions?.let { add("extensions = %L,\n", it.toCodeBlock()) }
				unindent()
				add(")\n")
			}
			add(codeBlock)
			
			// override fun append(
			//		name: String,
			//		value: String,
			//		maxAge: Int,
			//		expires: Long?,
			//		domain: String?,
			//		path: String?,
			//		secure: Boolean,
			//		httpOnly: Boolean,
			//		extensions: Map<String, String?>
			//	) {
			
			// val codeBlock = buildCodeBlock {
			//				add("cookie(\n")
			//				indent()
			//				add("name = %S,\n", model.name)
			//				add("value = %N,\n", model.varName)
			//				model.maxAge?.let { add("maxAge = %L,\n", it) }
			//				model.expires?.let {
			//					fileSpecBuilder.addImport(PackageNames.KTOR_UTIL_DATE, "GMTDate")
			//					add("expires = %L,\n", "GMTDate(${it}L)")
			//				}
			//				model.domain?.let { add("domain = %S,\n", it) }
			//				model.path?.let { add("path = %S,\n", it) }
			//				model.secure?.let { add("secure = %L,\n", it) }
			//				model.httpOnly?.let { add("httpOnly = %L,\n", it) }
			//				model.extensions?.let { add("extensions = %L,\n", it.toCodeBlock()) }
			//				unindent()
			//				add(")\n")
			//			}
			//			add(codeBlock)
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