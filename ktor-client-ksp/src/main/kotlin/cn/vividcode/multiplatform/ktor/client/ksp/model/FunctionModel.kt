package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.TypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/11 下午11:42
 *
 * 介绍：FunctionModel
 */
internal data class FunctionModel(
	val functionName: String,
	val requestType: RequestType,
	val url: String,
	val auth: Boolean,
	val parameterModels: List<ParameterModel>,
	val returnTypeName: TypeName,
	val queryModels: List<QueryModel>,
	val headerModels: List<HeaderModel>,
	val headersModels: List<HeadersModel>,
	val formModels: List<FormModel>,
	val pathModels: List<PathModel>,
	val bodyModel: BodyModel?
)