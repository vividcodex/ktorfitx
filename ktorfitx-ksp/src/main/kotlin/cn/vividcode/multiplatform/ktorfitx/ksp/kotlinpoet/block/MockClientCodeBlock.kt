package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/14 17:31
 *
 * 文件介绍：MockClientCodeBlock
 */
internal class MockClientCodeBlock(
	private val className: ClassName,
	private val mockModel: MockModel,
) : ClientCodeBlock {
	
	override fun CodeBlock.Builder.buildClientCodeBlock(
		funName: String,
		fullUrl: String,
		hasBuilder: Boolean,
		builder: CodeBlock.Builder.() -> Unit,
	) {
		UseImports += mockModel.provider
		UseImports.addImports(mockModel.status.packageName, mockModel.status.simpleNames.first())
		val buildUrl = if (fullUrl.isHttpOrHttps()) fullUrl else "\${this.ktorfit.baseUrl}$fullUrl"
		val provider = mockModel.provider.simpleName
		val status = "MockStatus.${mockModel.status.simpleName}"
		val leftRound = mockModel.delayRange[0]
		val rightRound = mockModel.delayRange.let { if (it.size == 1) it[0] else it[1] }
		val delayRange = "${leftRound}L..${rightRound}L"
		val mockClientCode = "this.mockClient.$funName(\"$buildUrl\", $provider, $status, $delayRange)"
		if (hasBuilder) {
			beginControlFlow(mockClientCode)
			builder()
			endControlFlow()
		} else {
			addStatement(mockClientCode)
		}
	}
	
	override fun CodeBlock.Builder.buildBearerAuthCodeBlock() {
		addStatement("this@${className.simpleName}.ktorfit.token?.let { bearerAuth(it()) }")
	}
	
	override fun CodeBlock.Builder.buildHeadersCodeBlock(
		headersModel: HeadersModel?,
		headerModels: List<HeaderModel>,
	) {
		beginControlFlow("headers")
		headersModel?.headerMap?.forEach { (name, value) ->
			addStatement("append(\"$name\", \"$value\"")
		}
		headerModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildQueriesCodeBlock(
		queryModels: List<QueryModel>,
	) {
		beginControlFlow("queries")
		queryModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildFormsCodeBlock(
		formModels: List<FormModel>,
	) {
		beginControlFlow("forms")
		formModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	fun CodeBlock.Builder.buildPathsCodeBlock(
		pathModels: List<PathModel>,
	) {
		beginControlFlow("paths")
		pathModels.forEach {
			val varName = it.varName.encryptVarName(it.encryptInfo)
			addStatement("append(\"${it.name}\", $varName)")
		}
		endControlFlow()
	}
	
	override fun CodeBlock.Builder.buildBodyCodeBlock(bodyModel: BodyModel) {
		addStatement("body(${bodyModel.varName})")
	}
}