package cn.ktorfitx.multiplatform.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.*
import cn.ktorfitx.common.ksp.util.expends.allClassNames
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.HttpClientCodeBlock
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.HttpCodeBlockBuilder
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.MockClientCodeBlock
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.WebSocketBuilder
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.AnyReturnStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.UnitReturnStructure
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

internal object ApiKotlinPoet {
	
	private const val TOKEN_VAR_NAME = "token"
	
	/**
	 * 文件
	 */
	fun getFileSpec(classStructure: ClassStructure): FileSpec {
		return buildFileSpec(classStructure.className) {
			fileSpecBuilderLocal.set(this)
			indent("\t")
			addType(getTypeSpec(classStructure))
			addProperties(getExpendPropertySpecs(classStructure))
			fileSpecBuilderLocal.remove()
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
		val typeName = classStructure.superinterface.copy(nullable = true)
		val propertySpec = buildPropertySpec("instance", typeName, KModifier.PRIVATE) {
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
			fileSpecBuilder.addImport(apiScopeClassName.packageName, apiScopeClassName.simpleName)
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
				when (returnStructure) {
					is UnitReturnStructure -> returns(ClassNames.Unit)
					is AnyReturnStructure -> {
						returnStructure.typeName.allClassNames.forEach { className ->
							val topLevelClassName = className.topLevelClassName()
							fileSpecBuilder.addImport(topLevelClassName.packageName, topLevelClassName.simpleNames)
						}
						returns(returnStructure.typeName)
					}
				}
				
			}
		}.toList()
	}
	
	private fun getParameterSpecs(models: List<ParameterModel>): List<ParameterSpec> {
		return models.map { buildParameterSpec(it.varName, it.typeName) }
	}
	
	private fun getCodeBlock(classStructure: ClassStructure, funStructure: FunStructure): CodeBlock {
		return buildCodeBlock {
			val tokenVarName = getTokenVarName(funStructure.funModels, funStructure.parameterModels)
			if (tokenVarName != null) {
				addStatement("val %N = this.config.token?.invoke()", tokenVarName)
			}
			val isWebSocket = funStructure.funModels.any { it is WebSocketModel }
			if (isWebSocket) {
				with(WebSocketBuilder(classStructure, funStructure)) {
					buildCodeBlock(tokenVarName)
				}
			} else {
				val isMockClient = funStructure.funModels.any { it is MockModel }
				val codeBlockKClass = if (isMockClient) MockClientCodeBlock::class else HttpClientCodeBlock::class
				with(HttpCodeBlockBuilder(classStructure, funStructure, codeBlockKClass)) {
					buildCodeBlock(tokenVarName)
				}
			}
		}
	}
	
	private fun getTokenVarName(
		funModels: List<FunModel>,
		parameterModels: List<ParameterModel>
	): String? {
		if (funModels.all { it !is BearerAuthModel }) {
			return null
		}
		var i = 0
		var varName = TOKEN_VAR_NAME
		val varNames = parameterModels.map { it.varName }
		while (varName in varNames) {
			varName = TOKEN_VAR_NAME + i++
		}
		return varName
	}
}