package cn.vividcode.multiplatform.ktor.client.ksp

import cn.vividcode.multiplatform.ktor.client.api.annotation.Api
import cn.vividcode.multiplatform.ktor.client.ksp.expends.generate
import cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.ApiKotlinPoet
import cn.vividcode.multiplatform.ktor.client.ksp.visitor.ApiVisitor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
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
	private val codeGenerator: CodeGenerator
) : SymbolProcessor {
	
	private val apiVisitor by lazy { ApiVisitor() }
	private val apiKotlinPoet by lazy { ApiKotlinPoet() }
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val annotatedList = resolver.getSymbolsWithAnnotation(Api::class.qualifiedName!!)
		val rets = annotatedList.filterNot { it.validate() && it is KSClassDeclaration && it.classKind == ClassKind.INTERFACE }.toMutableSet()
		(annotatedList - rets).forEach {
			val classStructure = it.accept(apiVisitor, Unit)
			if (classStructure != null) {
				val fileSpec = apiKotlinPoet.getFileSpec(classStructure)
				codeGenerator.generate(fileSpec, classStructure.className)
			} else rets += it
		}
		return rets.toList()
	}
}