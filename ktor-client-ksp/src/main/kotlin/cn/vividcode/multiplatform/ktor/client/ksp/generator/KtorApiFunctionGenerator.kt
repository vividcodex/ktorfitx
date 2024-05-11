package cn.vividcode.multiplatform.ktor.client.ksp.generator

import cn.vividcode.multiplatform.ktor.client.ksp.expends.className
import cn.vividcode.multiplatform.ktor.client.ksp.expends.generate
import cn.vividcode.multiplatform.ktor.client.ksp.model.KtorApiModel
import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.*

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/7 上午2:35
 *
 * 介绍：KtorApiFunctionGenerator
 */
internal class KtorApiFunctionGenerator(
	private val codeGenerator: CodeGenerator
) : Generator<KtorApiModel> {
	
	private companion object {
		private val ktorClientClassName = ClassName("cn.vividcode.multiplatform.ktor.client.api", "KtorClient")
	}
	
	override fun process(data: KtorApiModel) {
		val ktorApisClassName = ClassName("${data.namespace}.api.generate", "KtorApis")
		if (exists(ktorApisClassName)) return
		
		val fileSpecBuilder = FileSpec.builder(ktorApisClassName)
			.indent("\t")
		data.classDeclarations.forEach {
			val functionName = it.simpleName.asString().replaceFirstChar { it.lowercase() }
			fileSpecBuilder.addImport("${data.namespace}.api.generate.impl", "${it.simpleName.asString()}Impl")
			fileSpecBuilder.addProperty(getPropertySpec(functionName, it.className))
		}
		val fileSpec = fileSpecBuilder.build()
		codeGenerator.generate(fileSpec, ktorApisClassName.packageName, ktorApisClassName.simpleName)
	}
	
	private fun getPropertySpec(name: String, className: ClassName): PropertySpec {
		val funSpec = FunSpec.getterBuilder()
			.addStatement("return ${className.simpleName}Impl.getInstance(this)", className)
			.build()
		return PropertySpec.builder(name, className)
			.receiver(ktorClientClassName)
			.getter(funSpec)
			.build()
	}
}