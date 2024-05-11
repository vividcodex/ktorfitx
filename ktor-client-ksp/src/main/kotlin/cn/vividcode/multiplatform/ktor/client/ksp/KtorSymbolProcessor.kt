package cn.vividcode.multiplatform.ktor.client.ksp

import cn.vividcode.multiplatform.ktor.client.api.annotation.Api
import cn.vividcode.multiplatform.ktor.client.ksp.generator.KtorApiFunctionGenerator
import cn.vividcode.multiplatform.ktor.client.ksp.model.KtorApiModel
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.ApiVisitor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/3/23 22:13
 *
 * 介绍：KtorSymbolProcessor
 */
internal class KtorSymbolProcessor(
	private val codeGenerator: CodeGenerator,
	private val kspLogger: KSPLogger,
	private val namespace: String
) : SymbolProcessor {
	
	private val ktorApiFunctionGenerator by lazy { KtorApiFunctionGenerator(codeGenerator) }
	
	private val apiVisitor by lazy { ApiVisitor(codeGenerator, kspLogger, namespace) }
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val apiRets = resolver.getSymbolsWithAnnotation(Api::class.qualifiedName!!)
			.partition { it is KSClassDeclaration && it.validate() }
			.also {
				val apiClassDeclarations = it.first.mapNotNull {
					it.accept(apiVisitor, Unit)
				}
				ktorApiFunctionGenerator.process(KtorApiModel(namespace, apiClassDeclarations))
			}.second
		return apiRets
	}
}