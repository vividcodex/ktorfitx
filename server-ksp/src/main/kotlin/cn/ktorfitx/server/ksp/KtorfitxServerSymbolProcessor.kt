package cn.ktorfitx.server.ksp

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.kotlinpoet.RouteKotlinPoet
import cn.ktorfitx.server.ksp.visitor.RouteGeneratorVisitor
import cn.ktorfitx.server.ksp.visitor.RouteVisitor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec

internal class KtorfitxServerSymbolProcessor(
	private val codeGenerator: CodeGenerator
) : SymbolProcessor {
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val routeGeneratorModels = resolver.getSymbolsWithAnnotation(ClassNames.RouteGenerator.canonicalName)
			.filterIsInstance<KSFile>()
			.mapNotNull {
				val visitor = RouteGeneratorVisitor()
				it.accept(visitor, Unit)
			}
		
		val symbols = ClassNames.requestMethodClassNames
			.flatMap { resolver.getSymbolsWithAnnotation(it.canonicalName) }
			.filterIsInstance<KSFunctionDeclaration>()
			.onEach {
				val parent = it.parent
				it.compileCheck(parent != null && (parent is KSFile || (parent is KSClassDeclaration && parent.classKind == ClassKind.OBJECT))) {
					val functionName = it.simpleName.asString()
					"$functionName 方法只允许声明在 文件顶层 或 object 类中"
				}
			}
		
		val routeModels = symbols.mapNotNull {
			val visitor = RouteVisitor()
			it.accept(visitor, Unit)
		}
		routeGeneratorModels.forEach {
			val filterRouteModels = if (it.pathParent.isNotEmpty()) {
				routeModels.filter { model ->
					model.path.startsWith("${it.pathParent}/")
				}
			} else routeModels
			val fileSpec = RouteKotlinPoet.getFileSpec(it, filterRouteModels)
			codeGenerate(it.packageName, it.fileName, fileSpec)
		}
		return emptyList()
	}
	
	private fun codeGenerate(
		packageName: String,
		fileName: String,
		fileSpec: FileSpec
	) {
		codeGenerator.createNewFile(
			dependencies = Dependencies.ALL_FILES,
			packageName = packageName,
			fileName = fileName,
		).bufferedWriter().use {
			fileSpec.writeTo(it)
		}
	}
}