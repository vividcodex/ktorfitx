package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/6 上午7:48
 *
 * 介绍：ModelResolver
 */
internal sealed interface ModelResolver<out M> {
	
	/**
	 * 解析
	 */
	fun KSFunctionDeclaration.resolve(): M
	
	sealed interface Resolvers<out M> {
		
		fun resolves(functionDeclaration: KSFunctionDeclaration): List<M>
	}
}