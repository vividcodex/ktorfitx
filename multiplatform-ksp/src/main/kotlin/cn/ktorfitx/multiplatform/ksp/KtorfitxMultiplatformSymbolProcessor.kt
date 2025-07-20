package cn.ktorfitx.multiplatform.ksp

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.code
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.ApiKotlinPoet
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
		resolver.getSymbolsWithAnnotation(TypeNames.Api.canonicalName)
			.filterIsInstance<KSClassDeclaration>()
			.filter {
				if (!it.validate()) return@filter false
				val classKind = it.classKind
				val className = { it.simpleName.asString() }
				it.compileCheck(classKind == ClassKind.INTERFACE) {
					"${className()} 必须是 interface 类型的，而你使用了 ${classKind.code}"
				}
				it.compileCheck(Modifier.SEALED !in it.modifiers) {
					"${className()} 接口在当前版本中不支持 sealed interface，请使用 interface"
				}
				it.compileCheck(it.parentDeclaration !is KSClassDeclaration) {
					"${className()} 接口必须是顶层接口，不允许嵌套"
				}
				true
			}
			.forEach {
				val classModel = it.accept(ApiVisitor, Unit)
				val fileSpec = ApiKotlinPoet.getFileSpec(classModel)
				val className = classModel.className
				codeGenerator.createNewFile(
					dependencies = Dependencies.ALL_FILES,
					packageName = className.packageName,
					fileName = className.simpleName
				).bufferedWriter().use(fileSpec::writeTo)
			}
		return emptyList()
	}
}