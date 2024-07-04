package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.ValueParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/2 下午10:16
 *
 * 介绍：ValueParameterModelsResolver
 */
internal sealed interface ValueParameterModelResolver<R : ValueParameterModel> {
	
	/**
	 * 获取 ValueParameterModels
	 */
	fun KSFunctionDeclaration.getValueParameterModels(): List<R>
}