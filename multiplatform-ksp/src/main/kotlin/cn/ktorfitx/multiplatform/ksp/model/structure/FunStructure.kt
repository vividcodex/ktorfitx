package cn.ktorfitx.multiplatform.ksp.model.structure

import cn.ktorfitx.multiplatform.ksp.model.model.FunctionModel
import cn.ktorfitx.multiplatform.ksp.model.model.ParameterModel
import cn.ktorfitx.multiplatform.ksp.model.model.ValueParameterModel

internal class FunStructure(
	val funName: String,
	val returnStructure: ReturnStructure,
	val parameterModels: List<ParameterModel>,
	val functionModels: List<FunctionModel>,
	val valueParameterModels: List<ValueParameterModel>
)