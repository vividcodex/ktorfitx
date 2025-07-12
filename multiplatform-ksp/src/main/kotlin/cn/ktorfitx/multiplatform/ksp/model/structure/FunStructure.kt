package cn.ktorfitx.multiplatform.ksp.model.structure

import cn.ktorfitx.multiplatform.ksp.model.model.FunModel
import cn.ktorfitx.multiplatform.ksp.model.model.ParameterModel
import cn.ktorfitx.multiplatform.ksp.model.model.ValueParameterModel

internal class FunStructure(
	val funName: String,
	val returnStructure: ReturnStructure,
	val parameterModels: List<ParameterModel>,
	val funModels: List<FunModel>,
	val valueParameterModels: List<ValueParameterModel>
)