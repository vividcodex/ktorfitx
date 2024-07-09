package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.model.BodyModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.FormModel
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
			}.checkLegal(functionDeclaration)
		}
		
		/**
		 * 检查合法性
		 */
		private fun List<ValueParameterModel>.checkLegal(
			functionDeclaration: KSFunctionDeclaration
		): List<ValueParameterModel> {
			check(!(this.any { it is BodyModel } && this.any { it is FormModel })) {
				"${functionDeclaration.qualifiedName!!.asString()} 方法不能同时使用 @Body 和 @Form 注解"
			}
			return this
		}
	}
}