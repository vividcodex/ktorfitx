package cn.ktorfitx.multiplatform.ksp

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.code
import cn.ktorfitx.common.ksp.util.imports.UseImports
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.constants.Packages
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.ApiKotlinPoet
import cn.ktorfitx.multiplatform.ksp.visitor.ApiVisitor
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate

internal class KtorfitxMultiplatformSymbolProcessor(
	private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
	
	private val apiKotlinPoet by lazy { ApiKotlinPoet() }
	private val processedSymbols = mutableSetOf<String>()
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val annotatedList = resolver.getSymbolsWithAnnotation(ClassNames.Api.canonicalName)
		annotatedList.forEach { symbol ->
			if (!symbol.validate()) return@forEach
			val classDeclaration = symbol as? KSClassDeclaration ?: return@forEach
			
			val classKind = classDeclaration.classKind
			classDeclaration.compileCheck(classKind == ClassKind.INTERFACE) {
				val className = classDeclaration.simpleName.asString()
				"$className 必须是 interface 类型的，而你使用了 ${classKind.code}"
			}
			classDeclaration.compileCheck(Modifier.SEALED !in classDeclaration.modifiers) {
				val className = classDeclaration.simpleName.asString()
				"$className 接口在当前版本中不支持 sealed interface，请使用 interface"
			}
			
			if (classDeclaration.isRepeatProcessing) return@forEach
			resolver.processing(classDeclaration)
		}
		return emptyList()
	}
	
	/**
	 * 不允许处理
	 */
	private val KSClassDeclaration.isRepeatProcessing: Boolean
		get() {
			val qualifiedName = this.qualifiedName?.asString() ?: return true
			return !processedSymbols.add(qualifiedName)
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
		val importMap = UseImports.get().filter {
			!it.key.startsWith("io.ktor") && !it.key.startsWith(Packages.KTORFITX) && !it.key.startsWith("kotlin")
		}
		UseImports.clear()
		val ksFiles = importMap.flatMap { (packageName, simpleNames) ->
			simpleNames.map { this.getClassDeclarationByName("$packageName.$it")?.containingFile }
		} + classDeclaration.containingFile
		val ksFileArray = ksFiles.filterNotNull()
			.groupBy { it.filePath }
			.map { it.value.first() }
			.toTypedArray()
		return Dependencies(false, *ksFileArray)
	}
}