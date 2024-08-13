package cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet

import cn.vividcode.multiplatform.ktorfit.ksp.expends.*
import cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet.block.HttpClientCodeBlockBuilder
import cn.vividcode.multiplatform.ktorfit.ksp.kotlinpoet.block.MockClientCodeBlockBuilder
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.MockModel
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.ParameterModel
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktorfit.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 21:20
 *
 * 文件介绍：ApiKotlinPoet
 */
internal class ApiKotlinPoet {
	
	private val imports by lazy { mutableMapOf<String, MutableSet<String>>() }
	
	private var hasApiFunction = false
	private var hasMockClient = false
	private var hasHttpClient = false
	
	private companion object {
		
		private val ktorfitConfigClassName = ClassName("cn.vividcode.multiplatform.ktorfit.api.config", "KtorfitConfig")
		
		private val ktorfitClassName = ClassName("cn.vividcode.multiplatform.ktorfit.api", "Ktorfit")
		
		private val httpClientClassName = ClassName("io.ktor.client", "HttpClient")
		
		private val mockClientClassName = ClassName("cn.vividcode.multiplatform.ktorfit.api.mock", "MockClient")
	}
	
	/**
	 * 文件
	 */
	fun getFileSpec(classStructure: ClassStructure): FileSpec {
		initFunStructuresStatus(classStructure.funStructures)
		return buildFileSpec(classStructure.className) {
			indent("\t")
			addType(getTypeSpec(classStructure))
			addProperty(getExpendPropertySpec(classStructure))
			this@ApiKotlinPoet.imports.let {
				it.forEach(::addImport)
				it.clear()
			}
		}
	}
	
	private fun initFunStructuresStatus(funStructures: Sequence<FunStructure>) {
		this.hasApiFunction = funStructures.iterator().hasNext()
		this.hasMockClient = hasApiFunction && funStructures.any { it.functionModels.any { it is MockModel } }
		this.hasHttpClient = hasApiFunction && funStructures.any { it.functionModels.all { it !is MockModel } }
	}
	
	/**
	 * 实现类
	 */
	private fun getTypeSpec(classStructure: ClassStructure): TypeSpec {
		val primaryConstructorFunSpec = buildConstructorFunSpec {
			addModifiers(KModifier.PRIVATE)
			if (hasApiFunction) {
				addParameter("ktorfit", ktorfitConfigClassName)
			}
			if (hasHttpClient) {
				addParameter("httpClient", httpClientClassName)
			}
			if (hasMockClient) {
				addParameter("mockClient", mockClientClassName)
			}
		}
		return buildClassTypeSpec(classStructure.className) {
			addModifiers(classStructure.kModifier)
			addSuperinterface(classStructure.superinterface)
			primaryConstructor(primaryConstructorFunSpec)
			if (hasApiFunction) {
				val ktorfitConfigPropertySpec = buildPropertySpec("ktorfit", ktorfitConfigClassName, KModifier.PRIVATE) {
					initializer("ktorfit")
					mutable(false)
				}
				addProperty(ktorfitConfigPropertySpec)
			}
			if (hasHttpClient) {
				val httpClientPropertySpec = buildPropertySpec("httpClient", httpClientClassName, KModifier.PRIVATE) {
					initializer("httpClient")
					mutable(false)
				}
				addProperty(httpClientPropertySpec)
			}
			if (hasMockClient) {
				val mockClientPropertySpec = buildPropertySpec("mockClient", mockClientClassName, KModifier.PRIVATE) {
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
			val parameters = mutableListOf<String>()
			if (hasApiFunction) {
				parameters += "ktorClient.ktorfit"
			}
			if (hasHttpClient) {
				parameters += "ktorClient.httpClient"
			}
			if (hasMockClient) {
				parameters += "ktorClient.mockClient"
			}
			beginControlFlow("return instance ?: $simpleName(${parameters.joinToString()}).also")
			addStatement("instance = it")
			endControlFlow()
		}
		val funSpec = buildFunSpec("getInstance") {
			addModifiers(classStructure.kModifier)
			returns(classStructure.superinterface)
			if (hasApiFunction) {
				addParameter(
					"ktorClient",
					ktorfitClassName.parameterizedBy(classStructure.apiStructure.apiScopeClassName)
				)
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
			val simpleName = classStructure.className.simpleName
			val parameter = if (hasApiFunction) "this" else ""
			addStatement("return $simpleName.getInstance($parameter)")
		}
		val expendPropertyName = classStructure.superinterface.simpleName.replaceFirstChar { it.lowercase() }
		return buildPropertySpec(expendPropertyName, classStructure.superinterface, classStructure.kModifier) {
			receiver(ktorfitClassName.parameterizedBy(classStructure.apiStructure.apiScopeClassName))
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
			val isMockClient = funStructure.functionModels.any { it is MockModel }
			val buildCodeBlock = if (isMockClient) {
				MockClientCodeBlockBuilder(classStructure, funStructure, ::addImport)
			} else {
				HttpClientCodeBlockBuilder(classStructure, funStructure, ::addImport)
			}
			with(buildCodeBlock) {
				buildCodeBlock()
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