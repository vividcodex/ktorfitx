package cn.ktorfitx.multiplatform.ksp

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
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
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class KtorfitxMultiplatformSymbolProcessor(
	private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
	
	override fun process(resolver: Resolver): List<KSAnnotated> {
		val customHttpMethods = resolver.getCustomHttpMethodModels()
		resolver.generateApiImpls(customHttpMethods)
		return emptyList()
	}
	
	private fun Resolver.getCustomHttpMethodModels(): List<CustomHttpMethodModel> {
		return this.getSymbolsWithAnnotation(TypeNames.HttpMethod.canonicalName)
			.filterIsInstance<KSClassDeclaration>()
			.filter { it.validate() }
			.map {
				val classKind = it.classKind
				it.compileCheck(classKind == ClassKind.ANNOTATION_CLASS) {
					"@HttpMethod 只允许标注在 annotation class 上，而你的是 ${classKind.code}"
				}
				it.compileCheck(!it.isGeneric()) {
					"${it.simpleName.asString()} 注解不允许使用泛型"
				}
				fun validProperty(): Boolean {
					val properties = it.getAllProperties().toList()
					if (properties.size != 1) return false
					val property = properties.first()
					val typeName = property.type.toTypeName()
					if (typeName != TypeNames.String) return false
					val simpleName = property.simpleName.asString()
					return simpleName == "url"
				}
				it.compileCheck(validProperty()) {
					"${it.simpleName.asString()} 注解必须添加 val url: String 参数"
				}
				fun validTarget(): Boolean {
					val annotation = it.getKSAnnotationByType(TypeNames.Target) ?: return false
					val classNames = annotation.getClassNamesOrNull("allowedTargets") ?: return false
					if (classNames.size != 1) return false
					val className = classNames.first()
					return className == TypeNames.AnnotationTargetFunction
				}
				it.compileCheck(validTarget()) {
					"${it.simpleName.asString()} 注解必须标注 @Target(AnnotationTarget.FUNCTION)"
				}
				fun validRetention(): Boolean {
					val annotation = it.getKSAnnotationByType(TypeNames.Retention) ?: return false
					val className = annotation.getClassNameOrNull("value") ?: return false
					return className == TypeNames.AnnotationRetentionSource
				}
				it.compileCheck(validRetention()) {
					"${it.simpleName.asString()} 注解必须标注 @Retention(AnnotationRetention.SOURCE)"
				}
				val httpMethod = it.getKSAnnotationByType(TypeNames.HttpMethod)!!
				val method = httpMethod.getValueOrNull<String>("method")
					?.takeIf { method -> method.isNotBlank() }
					?: it.simpleName.asString()
				httpMethod.compileCheck(method.isValidHttpMethod()) {
					"${it.simpleName.asString()} 注解上的 $httpMethod 中使用了不合法的 Http Method 名称，只允许包含 A-Z 0-9 '-'"
				}
				httpMethod.compileCheck(TypeNames.httpMethods.all { it.simpleName != method }) {
					"${it.simpleName.asString()} 函数不允许使用 ${TypeNames.httpMethods.joinToString { it.simpleName }} 作为自定义 Http Method 名称"
				}
				CustomHttpMethodModel(method, it.toClassName())
			}
			.toList()
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