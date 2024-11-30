package cn.vividcode.multiplatform.ktorfitx.ksp

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.ApiKotlinPoet
import cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block.UseImports
import cn.vividcode.multiplatform.ktorfitx.ksp.visitor.ApiVisitor
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 22:13
 *
 * 文件介绍：KtorfitSymbolProcessor
 */
internal class KtorfitSymbolProcessor(
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {
	
	private val apiKotlinPoet by lazy { ApiKotlinPoet() }
	private val processedSymbols = mutableSetOf<String>()
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val annotatedList = resolver.getSymbolsWithAnnotation(KtorfitxQualifiers.API)
		annotatedList.forEach { symbol ->
			if (!symbol.validate()) return@forEach
			val classDeclaration = symbol as? KSClassDeclaration ?: return@forEach
			if (classDeclaration.disallowedProcessing) return@forEach
			resolver.processing(classDeclaration)
		}
		return emptyList()
	}
	
	/**
	 * 不允许处理
	 */
	private val KSClassDeclaration.disallowedProcessing: Boolean
		get() {
			val qualifiedName = this.qualifiedName?.asString() ?: return true
			return !processedSymbols.add(qualifiedName) ||      // 不允许重复执行
				this.classKind != ClassKind.INTERFACE ||        // 必须是 interface
				Modifier.SEALED in this.modifiers               // 不允许 sealed interface
		}
	
	/**
	 * 开始处理
	 */
	private fun Resolver.processing(classDeclaration: KSClassDeclaration) {
		val apiVisitor = ApiVisitor(this)
		val visitorResult = classDeclaration.accept(apiVisitor, Unit) ?: return
		val fileSpec = apiKotlinPoet.getFileSpec(visitorResult.classStructure)
		val className = visitorResult.classStructure.className
		codeGenerator.createNewFile(
			dependencies = getDependencies(classDeclaration),
			packageName = className.packageName,
			fileName = className.simpleName
		).bufferedWriter().use {
			fileSpec.writeTo(it)
		}
	}
	
	/**
	 * 获取源文件
	 */
	private fun Resolver.getDependencies(classDeclaration: KSClassDeclaration): Dependencies {
		val importMap = UseImports.get().filterNot {
			it.key.startsWith("io.ktor") ||
				it.key.startsWith(KtorfitxQualifiers.PACKAGE_API)
		}
		UseImports.clear()
		val ksFiles = importMap.flatMap { (packageName, simpleNames) ->
			simpleNames.map {
				val ksFile = this.getClassDeclarationByName("$packageName.$it")?.containingFile
				if (ksFile == null) {
					logger.warn("$packageName.$it 未能获取它的源文件")
				}
				ksFile
			}
		} + classDeclaration.containingFile
		val ksFileArray = ksFiles.filterNotNull()
			.groupBy { it.filePath }
			.map { it.value.first() }
			.toTypedArray()
		return Dependencies(false, *ksFileArray)
	}
}