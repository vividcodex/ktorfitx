package cn.vividcode.multiplatform.ktor.client.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * 项目名称：vividcode-multiplatform
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 22:14
 *
 * 文件介绍：KtorSymbolProcessorProvider
 */
internal class KtorSymbolProcessorProvider : SymbolProcessorProvider {

	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		return KtorSymbolProcessor(environment.codeGenerator)
	}
}