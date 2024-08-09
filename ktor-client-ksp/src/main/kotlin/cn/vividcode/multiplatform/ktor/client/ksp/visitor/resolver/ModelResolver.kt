package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 7:48
 *
 * 文件介绍：ModelResolver
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