package cn.ktorfitx.multiplatform.ksp

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.code
import cn.ktorfitx.common.ksp.util.expends.getCustomHttpMethodModels
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.ApiKotlinPoet
import cn.ktorfitx.multiplatform.ksp.model.CustomHttpMethodModel
import cn.ktorfitx.multiplatform.ksp.visitor.ApiVisitor
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
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val customHttpMethods = resolver.getCustomHttpMethodModels(
			httpMethod = TypeNames.HttpMethod,
			httpMethods = TypeNames.httpMethods,
			parameterName = "url",
			transform = ::CustomHttpMethodModel
		)
		resolver.generateApiImpls(customHttpMethods)
		return emptyList()
	}
	
	private fun Resolver.generateApiImpls(
		customHttpMethodModels: List<CustomHttpMethodModel>
	) {
		this.getSymbolsWithAnnotation(TypeNames.Api.canonicalName)
			.filterIsInstance<KSClassDeclaration>()
			.filter { it.validate() }
			.forEach {
				val classKind = it.classKind
				it.compileCheck(classKind == ClassKind.INTERFACE) {
					"@Api 只允许标注在 interface 上，而你的是 ${classKind.code}"
				}
				it.compileCheck(Modifier.SEALED !in it.modifiers) {
					"${it.simpleName.asString()} 接口不支持 sealed 修饰符"
				}
				it.compileCheck(it.parentDeclaration !is KSClassDeclaration) {
					"${it.simpleName.asString()} 接口必须是顶层接口"
				}
				val classModel = it.accept(ApiVisitor, customHttpMethodModels)
				val fileSpec = ApiKotlinPoet.getFileSpec(classModel)
				val className = classModel.className
				codeGenerator.createNewFile(
					dependencies = Dependencies.ALL_FILES,
					packageName = className.packageName,
					fileName = className.simpleName
				).bufferedWriter().use(fileSpec::writeTo)
			}
	}
}