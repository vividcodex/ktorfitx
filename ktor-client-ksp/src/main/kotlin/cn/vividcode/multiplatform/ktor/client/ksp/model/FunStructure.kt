package cn.vividcode.multiplatform.ktor.client.ksp.model

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午3:48
 *
 * 介绍：FunStructure
 */
internal data class FunStructure(
	val funName: String,
	val returnStructure: ReturnStructure,
	val functionModels: List<FunctionModel>,
	val valueParameterModels: List<ValueParameterModel>,
)