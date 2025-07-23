package cn.ktorfitx.server.ksp

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.getCustomHttpMethodModels
import cn.ktorfitx.server.ksp.constants.TypeNames
import cn.ktorfitx.server.ksp.kotlinpoet.RouteKotlinPoet
import cn.ktorfitx.server.ksp.model.CustomHttpMethodModel
import cn.ktorfitx.server.ksp.model.FunModel
import cn.ktorfitx.server.ksp.model.RouteGeneratorModel
import cn.ktorfitx.server.ksp.visitor.RouteGeneratorVisitor
import cn.ktorfitx.server.ksp.visitor.RouteVisitor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate

internal class KtorfitxServerSymbolProcessor(
	private val codeGenerator: CodeGenerator
) : SymbolProcessor {
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val customHttpMethodModels = resolver.getCustomHttpMethodModels(
			httpMethod = TypeNames.HttpMethod,
			httpMethods = TypeNames.httpMethods,
			transform = ::CustomHttpMethodModel
		)
		val routeGeneratorModels = resolver.getRouteGeneratorModels()
		val funModels = resolver.getFunModels()
		generateRouteGenerators(
			customHttpMethodModels,
			routeGeneratorModels,
			funModels,
		)
		return emptyList()
	}
	
	private fun Resolver.getFunModels(): List<FunModel> {
		return TypeNames.routes
			.flatMap { this.getSymbolsWithAnnotation(it.canonicalName) }
			.filterIsInstance<KSFunctionDeclaration>()
			.map {
				val parent = it.parent
				it.compileCheck(parent != null && (parent is KSFile || (parent is KSClassDeclaration && parent.classKind == ClassKind.OBJECT))) {
					val functionName = it.simpleName.asString()
					"$functionName 函数只允许声明在 文件顶层 或 object 类中"
				}
				val visitor = RouteVisitor()
				it.accept(visitor, Unit)
			}
	}
	
	private fun Resolver.getRouteGeneratorModels(): List<RouteGeneratorModel> {
		return this.getSymbolsWithAnnotation(TypeNames.RouteGenerator.canonicalName)
			.filterIsInstance<KSFile>()
			.filter { it.validate() }
			.mapNotNull {
				val visitor = RouteGeneratorVisitor()
				it.accept(visitor, Unit)
			}
			.toList()
	}
	
	private fun generateRouteGenerators(
		customHttpMethodModels: List<CustomHttpMethodModel>,
		routeGeneratorModels: List<RouteGeneratorModel>,
		funModels: List<FunModel>,
	) {
		routeGeneratorModels.forEach { model ->
			val predicate: (FunModel) -> Boolean = when {
				model.includeGroups.isEmpty() && model.excludeGroups.isEmpty() -> ({ it.group == null })
				model.includeGroups.isNotEmpty() -> ({ it.group in model.includeGroups })
				else -> ({ it.group !in model.excludeGroups })
			}
			val fileSpec = RouteKotlinPoet()
				.getFileSpec(model, funModels.filter(predicate))
			codeGenerator.createNewFile(
				dependencies = Dependencies.ALL_FILES,
				packageName = model.packageName,
				fileName = model.fileName
			).bufferedWriter().use {
				fileSpec.writeTo(it)
			}
		}
	}
}