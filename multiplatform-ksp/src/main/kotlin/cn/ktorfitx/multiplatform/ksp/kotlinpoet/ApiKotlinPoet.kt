package cn.ktorfitx.multiplatform.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.*
import cn.ktorfitx.common.ksp.util.imports.UseImports
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.HttpClientCodeBlock
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.HttpCodeBlockBuilder
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.MockClientCodeBlock
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.WebSocketBuilder
import cn.ktorfitx.multiplatform.ksp.model.model.MockModel
import cn.ktorfitx.multiplatform.ksp.model.model.ParameterModel
import cn.ktorfitx.multiplatform.ksp.model.model.WebSocketModel
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

internal class ApiKotlinPoet {
	
	private var empty = false
	
	/**
	 * 文件
	 */
	fun getFileSpec(classStructure: ClassStructure): FileSpec {
		return buildFileSpec(classStructure.className) {
			indent("\t")
			addType(getTypeSpec(classStructure))
			addProperties(getExpendPropertySpecs(classStructure))
			UseImports.get().forEach(::addImport)
		}
	}
	
	/**
	 * 实现类
	 */
	private fun getTypeSpec(classStructure: ClassStructure): TypeSpec {
		val primaryConstructorFunSpec = buildConstructorFunSpec {
			addModifiers(KModifier.PRIVATE)
			addParameter("config", ClassNames.KtorfitConfig)
		}
		return buildClassTypeSpec(classStructure.className) {
			addModifiers(KModifier.PRIVATE)
			addSuperinterface(classStructure.superinterface)
			primaryConstructor(primaryConstructorFunSpec)
			val ktorfitConfigPropertySpec = buildPropertySpec("config", ClassNames.KtorfitConfig, KModifier.PRIVATE) {
				initializer("config")
				mutable(false)
			}
			addProperty(ktorfitConfigPropertySpec)
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
			beginControlFlow("return instance ?: %T(ktorfit.config).also", classStructure.className)
			addStatement("instance = it")
			endControlFlow()
		}
		val funSpecs = classStructure.apiStructure.apiScopeClassNames.map { apiScopeClassName ->
			val jvmNameAnnotationSpec = buildAnnotationSpec(JvmName::class) {
				addMember("%S", "getInstanceBy${apiScopeClassName.simpleName}")
			}
			buildFunSpec("getInstance") {
				addAnnotation(jvmNameAnnotationSpec)
				addModifiers(classStructure.kModifier)
				returns(classStructure.superinterface)
				addParameter(
					"ktorfit",
					ClassNames.Ktorfit.parameterizedBy(apiScopeClassName)
				)
				addCode(codeBlock)
			}
		}
		return buildCompanionObjectTypeSpec {
			addModifiers(classStructure.kModifier)
			addProperty(propertySpec)
			addFunctions(funSpecs)
		}
	}
	
	/**
	 * 扩展函数
	 */
	private fun getExpendPropertySpecs(classStructure: ClassStructure): List<PropertySpec> {
		val expendPropertyName = classStructure.superinterface.simpleName.replaceFirstChar { it.lowercase() }
		return classStructure.apiStructure.apiScopeClassNames.map { apiScopeClassName ->
			UseImports += apiScopeClassName
			val jvmNameAnnotationSpec = buildAnnotationSpec(JvmName::class) {
				addMember("%S", "${expendPropertyName}By${apiScopeClassName.simpleName}")
			}
			val getterFunSpec = buildGetterFunSpec {
				addAnnotation(jvmNameAnnotationSpec)
				addStatement("return %T.getInstance(this)", classStructure.className)
			}
			buildPropertySpec(expendPropertyName, classStructure.superinterface, classStructure.kModifier) {
				receiver(ClassNames.Ktorfit.parameterizedBy(apiScopeClassName))
				getter(getterFunSpec)
			}
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
				val returnStructure = it.returnStructure
				UseImports += returnStructure.classNames
				returns(returnStructure.typeName)
			}
		}.toList()
	}
	
	private fun getParameterSpecs(models: List<ParameterModel>): List<ParameterSpec> {
		return models.map { buildParameterSpec(it.varName, it.typeName) }
	}
	
	private fun getCodeBlock(classStructure: ClassStructure, funStructure: FunStructure): CodeBlock {
		return buildCodeBlock {
			val isWebSocket = funStructure.functionModels.any { it is WebSocketModel }
			if (isWebSocket) {
				with(WebSocketBuilder(classStructure, funStructure)) {
					buildCodeBlock()
				}
			} else {
				val isMockClient = funStructure.functionModels.any { it is MockModel }
				val codeBlockKClass = if (isMockClient) MockClientCodeBlock::class else HttpClientCodeBlock::class
				with(HttpCodeBlockBuilder(classStructure, funStructure, codeBlockKClass)) {
					buildCodeBlock()
				}
			}
		}
	}
}