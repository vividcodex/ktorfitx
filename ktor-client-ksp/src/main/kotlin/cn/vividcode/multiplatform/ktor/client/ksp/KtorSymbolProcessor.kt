package cn.vividcode.multiplatform.ktor.client.ksp

import cn.vividcode.multiplatform.ktor.client.api.annotation.Api
import cn.vividcode.multiplatform.ktor.client.ksp.expends.generate
import cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.ApiKotlinPoet
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.ApiVisitor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
	private val codeGenerator: CodeGenerator
) : SymbolProcessor {
	
	private val apiVisitor by lazy { ApiVisitor() }
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val annotatedList = resolver.getSymbolsWithAnnotation(Api::class.qualifiedName!!)
		val rets = annotatedList.filterNot { it.validate() && it is KSClassDeclaration }.toMutableSet()
		runBlocking {
			(annotatedList - rets).forEach {
				launch {
					process(it)
				}
			}
		}
		return rets.toList()
	}
	
	private suspend fun process(ksAnnotated: KSAnnotated) = coroutineScope {
		val classStructure = ksAnnotated.accept(apiVisitor, Unit) ?: return@coroutineScope
		val apiKotlinPoet = ApiKotlinPoet()
		val fileSpec = apiKotlinPoet.getFileSpec(classStructure)
		codeGenerator.generate(fileSpec, classStructure.className)
	}
}