package cn.ktorfitx.server.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class KtorfitxServerSymbolProcessorProvider : SymbolProcessorProvider {
	
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		kspLoggerLocal.set(environment.logger)
		return KtorfitxServerSymbolProcessor()
	}
}