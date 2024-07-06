package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet

import cn.vividcode.multiplatform.ktor.client.api.KtorClient
import cn.vividcode.multiplatform.ktor.client.api.config.KtorConfig
import cn.vividcode.multiplatform.ktor.client.api.mock.MockClient
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
 * 介绍：ApiKotlinPoet
 */
internal class ApiKotlinPoet {
	
	private val imports by lazy { mutableMapOf<String, MutableSet<String>>() }
	
	private var includeMock = false
	private var hasFunction = true
	
	/**
	 * 文件
	 */
	fun getFileSpec(classStructure: ClassStructure): FileSpec {
		initFunStructuresStatus(classStructure.funStructures)
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
	
	private fun initFunStructuresStatus(funStructures: Sequence<FunStructure>) {
		this.hasFunction = funStructures.iterator().hasNext()
		this.includeMock = this.hasFunction && funStructures.any { funStructure ->
			funStructure.functionModels.any { it is MockModel }
		}
	}
	
	/**
	 * 实现类
	 */
	private fun getTypeSpec(classStructure: ClassStructure): TypeSpec {
		val primaryConstructorFunSpec = buildConstructorFunSpec {
			addModifiers(KModifier.PRIVATE)
			if (hasFunction) {
				addParameter("ktorConfig", KtorConfig::class)
				addParameter("httpClient", HttpClient::class)
			}
			if (includeMock) {
				addParameter("mockClient", MockClient::class)
			}
		}
		return buildClassTypeSpec(classStructure.className) {
			addModifiers(classStructure.kModifier)
			addSuperinterface(classStructure.superinterface)
			primaryConstructor(primaryConstructorFunSpec)
			if (hasFunction) {
				val ktorConfigPropertySpec = buildPropertySpec("ktorConfig", KtorConfig::class, KModifier.PRIVATE) {
					initializer("ktorConfig")
					mutable(false)
				}
				val httpClientPropertySpec = buildPropertySpec("httpClient", HttpClient::class, KModifier.PRIVATE) {
					initializer("httpClient")
					mutable(false)
				}
				addProperty(ktorConfigPropertySpec)
				addProperty(httpClientPropertySpec)
			}
			if (includeMock) {
				val mockClientPropertySpec = buildPropertySpec("mockClient", MockClient::class, KModifier.PRIVATE) {
					initializer("mockClient")
					mutable(false)
				}
				addProperty(mockClientPropertySpec)
			}
			addType(getCompanionObjectBuilder(classStructure))
			addFunctions(getFunSpecs(classStructure))
		}
	}
	
	/**
	 * 伴生对象
	 */
	private fun getCompanionObjectBuilder(classStructure: ClassStructure): TypeSpec {
		val type = classStructure.superinterface.copy(nullable = true)
		val propertySpec = buildPropertySpec("instance", type, KModifier.PRIVATE) {
			initializer("null")
			mutable(true)
		}
		val codeBlock = buildCodeBlock {
			val simpleName = classStructure.className.simpleName
			if (includeMock) {
				beginControlFlow("return instance ?: $simpleName(ktorConfig, httpClient, mockClient).also")
			} else if (hasFunction) {
				beginControlFlow("return instance ?: $simpleName(ktorConfig, httpClient).also")
			} else {
				beginControlFlow("return instance ?: $simpleName().also")
			}
			addStatement("instance = it")
			endControlFlow()
		}
		val funSpec = buildFunSpec("getInstance") {
			addModifiers(classStructure.kModifier)
			returns(classStructure.superinterface)
			if (hasFunction) {
				addParameter("ktorConfig", KtorConfig::class)
				addParameter("httpClient", HttpClient::class)
			}
			if (includeMock) {
				addParameter("mockClient", MockClient::class)
			}
			addCode(codeBlock)
		}
		return buildCompanionObjectTypeSpec {
			addModifiers(classStructure.kModifier)
			addProperty(propertySpec)
			addFunction(funSpec)
		}
	}
	
	/**
	 * 扩展函数
	 */
	private fun getExpendPropertySpec(classStructure: ClassStructure): PropertySpec {
		val getterFunSpec = buildGetterFunSpec {
			if (includeMock) {
				addStatement("return ${classStructure.className.simpleName}.getInstance(ktorConfig, httpClient, mockClient)")
			} else if (hasFunction) {
				addStatement("return ${classStructure.className.simpleName}.getInstance(ktorConfig, httpClient)")
			} else {
				addStatement("return ${classStructure.className.simpleName}.getInstance()")
			}
		}
		val name = classStructure.superinterface.simpleName
			.replaceFirstChar { it.lowercase() }
		return buildPropertySpec(name, classStructure.superinterface, classStructure.kModifier) {
			receiver(KtorClient::class.asClassName().parameterizedBy(classStructure.apiStructure.apiScopeClassName))
			getter(getterFunSpec)
		}
	}
	
	/**
	 * 实现方法
	 */
	private fun getFunSpecs(classStructure: ClassStructure): List<FunSpec> {
		return classStructure.funStructures.map {
			buildFunSpec(it.funName) {
				addModifiers(KModifier.SUSPEND, KModifier.OVERRIDE)
				addParameters(getParameterSpecs(it.parameterModels))
				addCode(getCodeBlock(classStructure, it))
				returns(it.returnStructure.typeName)
			}
		}.toList()
	}
	
	private fun getParameterSpecs(models: List<ParameterModel>): List<ParameterSpec> {
		return models.map { buildParameterSpec(it.varName, it.typeName) }
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