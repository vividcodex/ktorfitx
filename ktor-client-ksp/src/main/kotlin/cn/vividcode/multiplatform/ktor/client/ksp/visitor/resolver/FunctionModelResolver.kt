package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.model.FunctionModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/2 下午10:20
 *
 * 介绍：FunctionModelResolver
 */
internal sealed interface FunctionModelResolver<out R : FunctionModel> : ModelResolver<R?> {
	
	companion object : ModelResolver.Resolvers<FunctionModel> {
		
		override fun resolves(functionDeclaration: KSFunctionDeclaration): List<FunctionModel> {
			return FunctionModelResolver::class.sealedSubclasses.mapNotNull {
				with(it.objectInstance!!) {
					functionDeclaration.resolve()
				}
			}
		}
	}
}