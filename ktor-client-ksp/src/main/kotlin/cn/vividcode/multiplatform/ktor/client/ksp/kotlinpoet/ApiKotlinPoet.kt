package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet

import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.ksp.expends.*
import cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.block.KtorCodeBlock
import cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.block.MockCodeBlock
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.MockModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ParameterModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.ktor.client.*

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午9:20
 *
 * 介绍：ApiPoet
 */
internal class ApiKotlinPoet {
	
	private val imports by lazy { mutableMapOf<String, MutableSet<String>>() }
	
	@Synchronized
	fun getFileSpec(classStructure: ClassStructure): FileSpec {
		return buildFileSpec(classStructure.className) {
			indent("\t")
			addType(getTypeSpec(classStructure))
			addProperty(getExpendPropertySpec(classStructure))
			this@ApiKotlinPoet.imports.forEach { (packageName, simpleNames) ->
				addImport(packageName, simpleNames)
			}
			this@ApiKotlinPoet.imports.clear()
		}
	}
	
	private fun getTypeSpec(classStructure: ClassStructure): TypeSpec {
		val primaryConstructorFunSpec = buildConstructorFunSpec {
			addModifiers(KModifier.PRIVATE)
			addParameter("ktorConfig", KtorConfig::class)
			addParameter("httpClient", HttpClient::class)
		}
		val ktorConfigPropertySpec = buildPropertySpec("ktorConfig", KtorConfig::class, KModifier.PRIVATE) {
			initializer("ktorConfig")
			mutable(false)
		}
		val httpClientPropertySpec = buildPropertySpec("httpClient", HttpClient::class, KModifier.PRIVATE) {
			initializer("httpClient")
			mutable(false)
		}
		return buildClassTypeSpec(classStructure.className) {
			addModifiers(KModifier.PUBLIC)
			addSuperinterface(classStructure.superinterface)
			primaryConstructor(primaryConstructorFunSpec)
			addProperty(ktorConfigPropertySpec)
			addProperty(httpClientPropertySpec)
			addType(getCompanionObjectBuilder(classStructure))
			addFunctions(getFunSpecs(classStructure))
		}
	}
	
	private fun getCompanionObjectBuilder(classStructure: ClassStructure): TypeSpec {
		val type = classStructure.superinterface.copy(nullable = true)
		val propertySpec = buildPropertySpec("instance", type, KModifier.PRIVATE) {
			initializer("null")
			mutable(true)
		}
		val codeBlock = buildCodeBlock {
			beginControlFlow("return instance ?: ${classStructure.className.simpleName}(ktorConfig, httpClient).also")
			addStatement("instance = it")
			endControlFlow()
		}
		val funSpec = buildFunSpec("getInstance") {
			addModifiers(KModifier.PUBLIC)
			returns(classStructure.superinterface)
			addParameter("ktorConfig", KtorConfig::class)
			addParameter("httpClient", HttpClient::class)
			addCode(codeBlock)
		}
		return buildCompanionObjectTypeSpec {
			addProperty(propertySpec)
			addFunction(funSpec)
		}
	}
	
	private fun getExpendPropertySpec(classStructure: ClassStructure): PropertySpec {
		val getterFunSpec = buildGetterFunSpec {
			addStatement("return ${classStructure.className.simpleName}.getInstance(this.ktorConfig, this.httpClient)")
		}
		val name = classStructure.superinterface.simpleName
			.replaceFirstChar { it.lowercase() }
		return buildPropertySpec(name, classStructure.superinterface) {
			receiver(KtorClient::class.asClassName().parameterizedBy(classStructure.apiStructure.apiScopeClassName))
			getter(getterFunSpec)
		}
	}
	
	private fun getFunSpecs(classStructure: ClassStructure): List<FunSpec> {
		return classStructure.funStructures.map {
			getFunSpec(classStructure, it)
		}.toList()
	}
	
	private fun getFunSpec(classStructure: ClassStructure, funStructure: FunStructure): FunSpec {
		return buildFunSpec(funStructure.funName) {
			addModifiers(KModifier.SUSPEND, KModifier.OVERRIDE)
			addParameters(getParameterSpecs(funStructure.parameterModels))
			addCode(getCodeBlock(classStructure, funStructure))
			returns(funStructure.returnStructure.typeName)
		}
	}
	
	private fun getParameterSpecs(models: List<ParameterModel>): List<ParameterSpec> {
		return models.map {
			buildParameterSpec(it.varName, it.typeName)
		}
	}
	
	private fun getCodeBlock(classStructure: ClassStructure, funStructure: FunStructure): CodeBlock {
		return buildCodeBlock {
			val mockModel = funStructure.functionModels
				.filterIsInstance<MockModel>()
				.firstOrNull()
			if (mockModel != null) {
				with(MockCodeBlock(::addImport)) {
					buildMockCodeBlock(classStructure, funStructure, mockModel)
				}
			} else {
				with(KtorCodeBlock(::addImport)) {
					buildKtorCodeBlock(classStructure, funStructure)
				}
			}
		}
	}
	
	/**
	 * 添加包
	 */
	private fun addImport(packageName: String, vararg simpleNames: String) {
		val simpleNameSet = this.imports.getOrPut(packageName) { mutableSetOf() }
		simpleNameSet += simpleNames
	}
}