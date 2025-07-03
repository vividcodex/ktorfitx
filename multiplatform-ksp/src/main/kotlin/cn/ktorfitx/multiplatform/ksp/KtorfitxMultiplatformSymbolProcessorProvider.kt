package cn.ktorfitx.multiplatform.ksp

import cn.ktorfitx.common.ksp.util.check.kspLoggerLocal
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class KtorfitxMultiplatformSymbolProcessorProvider : SymbolProcessorProvider {
	
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		kspLoggerLocal.set(environment.logger)
		return KtorfitxMultiplatformSymbolProcessor(environment.codeGenerator)
	}
}