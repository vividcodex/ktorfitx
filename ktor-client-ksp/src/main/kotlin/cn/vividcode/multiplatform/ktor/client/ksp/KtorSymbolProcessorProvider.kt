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
	
	private companion object {
		private const val ERROR_MESSAGE = "请在build.gradle.kts中配置 kotlin.arg(\"namespace\", \"<包名>\""
	}
	
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		val namespace = environment.options["namespace"] ?: error(ERROR_MESSAGE)
		return KtorSymbolProcessor(
			environment.codeGenerator,
			environment.logger,
			namespace
		)
	}
}