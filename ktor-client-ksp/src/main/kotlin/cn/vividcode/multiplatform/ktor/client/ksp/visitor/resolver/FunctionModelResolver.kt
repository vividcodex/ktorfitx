package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.ksp.model.FunctionModel
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
internal sealed interface FunctionModelResolver<R : FunctionModel> {
	
	/**
	 * 获取 FunctionModel
	 */
	fun KSFunctionDeclaration.getFunctionModel(): R?
}