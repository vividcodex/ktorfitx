package cn.vividcode.multiplatform.ktor.client.ksp.model.structure

import cn.vividcode.multiplatform.ktor.client.ksp.model.model.FunctionModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ParameterModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ValueParameterModel

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
	val parameterModels: List<ParameterModel>,
	val functionModels: List<FunctionModel>,
	val valueParameterModels: List<ValueParameterModel>
)