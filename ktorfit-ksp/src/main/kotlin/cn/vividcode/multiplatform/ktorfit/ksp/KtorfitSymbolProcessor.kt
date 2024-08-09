package cn.vividcode.multiplatform.ktorfit.ksp

import cn.vividcode.multiplatform.ktorfit.annotation.Api
import cn.vividcode.multiplatform.ktorfit.ksp.expends.generate
import cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet.ApiKotlinPoet
import cn.vividcode.multiplatform.ktorfit.ksp.visitor.ApiVisitor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 22:13
 *
 * 文件介绍：KtorfitSymbolProcessor
 */
internal class KtorfitSymbolProcessor(
	private val codeGenerator: CodeGenerator
) : SymbolProcessor {
	
	private val apiVisitor by lazy { ApiVisitor() }
	private val apiKotlinPoet by lazy { ApiKotlinPoet() }
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val annotatedList = resolver.getSymbolsWithAnnotation(Api::class.qualifiedName!!)
		val rets = mutableListOf<KSAnnotated>()
		annotatedList.forEach {
			if (it.validate()) {
				rets += it
			}
			if (it is KSClassDeclaration && it.classKind == ClassKind.INTERFACE) {
				val classStructure = it.accept(apiVisitor, Unit)
				if (classStructure != null) {
					val fileSpec = apiKotlinPoet.getFileSpec(classStructure)
					codeGenerator.generate(fileSpec, classStructure.className)
				}
			}
		}
		return rets
	}
}