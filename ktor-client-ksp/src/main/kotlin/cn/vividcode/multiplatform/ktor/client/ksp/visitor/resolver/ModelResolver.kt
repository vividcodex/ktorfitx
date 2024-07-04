package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.FunctionModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.ValueParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午1:03
 *
 * 介绍：ModelResolver
 */
internal data object ModelResolvers {
	
	private val functionModelResolvers by lazy {
		listOf(
			HeadersModelResolver,
			MockModelResolver,
			ApiModelResolver
		)
	}
	
	private val valueParameterModelsResolver by lazy {
		listOf(
			EncryptValueParameterModelsResolver,
			BodyModelResolver
		)
	}
	
	fun getFunctionModels(functionDeclaration: KSFunctionDeclaration): List<FunctionModel> {
		return functionModelResolvers.mapNotNull {
			with(it) {
				functionDeclaration.getFunctionModel()
			}
		}
	}
	
	fun getValueParameterModels(functionDeclaration: KSFunctionDeclaration): List<ValueParameterModel> {
		return valueParameterModelsResolver.flatMap {
			with(it) {
				functionDeclaration.getValueParameterModels()
			}
		}
	}
}