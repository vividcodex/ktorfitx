package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.ksp.model.model.FunctionModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/2 22:20
 *
 * 文件介绍：FunctionModelResolver
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