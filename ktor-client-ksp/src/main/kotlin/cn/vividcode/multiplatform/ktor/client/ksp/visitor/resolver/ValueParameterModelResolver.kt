package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ValueParameterModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/2 下午10:16
 *
 * 介绍：ValueParameterModelResolver
 */
internal sealed interface ValueParameterModelResolver<out M : ValueParameterModel> : ModelResolver<List<M>> {
	
	companion object : ModelResolver.Resolvers<ValueParameterModel> {
		
		override fun resolves(functionDeclaration: KSFunctionDeclaration): List<ValueParameterModel> {
			return ValueParameterModelResolver::class.sealedSubclasses.flatMap {
				with(it.objectInstance!!) {
					functionDeclaration.resolve()
				}
			}
		}
	}
}