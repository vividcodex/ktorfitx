package cn.vividcode.multiplatform.ktor.client.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/3/23 22:14
 *
 * 介绍：KtorSymbolProcessorProvider
 */
internal class KtorSymbolProcessorProvider : SymbolProcessorProvider {
	
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		return KtorSymbolProcessor(environment.codeGenerator)
	}
}