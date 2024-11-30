package cn.vividcode.multiplatform.ktorfitx.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 22:14
 *
 * 文件介绍：KtorfitSymbolProcessorProvider
 */
internal class KtorfitSymbolProcessorProvider : SymbolProcessorProvider {
	
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
		KtorfitSymbolProcessor(environment.codeGenerator, environment.logger)
}