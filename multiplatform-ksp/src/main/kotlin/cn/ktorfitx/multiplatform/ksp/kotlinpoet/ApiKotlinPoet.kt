package cn.ktorfitx.multiplatform.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.*
import cn.ktorfitx.common.ksp.util.expends.replaceFirstToLowercase
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.HttpClientCodeBlock
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.HttpCodeBlockBuilder
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.MockClientCodeBlock
import cn.ktorfitx.multiplatform.ksp.kotlinpoet.block.WebSocketCodeBuilder
import cn.ktorfitx.multiplatform.ksp.model.model.*
import cn.ktorfitx.multiplatform.ksp.model.structure.ClassStructure
import cn.ktorfitx.multiplatform.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal object ApiKotlinPoet {
	
	private const val TOKEN_VAR_NAME = "token"
	
	private val fileComment = """
        该文件是由 cn.ktorfitx:multiplatform-ksp 在编译期间根据注解生成的代码，
        所有手动修改将会在下次构建时被覆盖，
        若需修改行为，请修改对应的注解或源代码定义，而不是此文件本身。
        
        生成时间：%L
        """.trimIndent()
	
	/**
	 * 文件
	 */
	fun getFileSpec(classStructure: ClassStructure): FileSpec {
		return buildFileSpec(classStructure.className) {
			fileSpecBuilderLocal.set(this)
			addFileComment(fileComment, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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
			addParameter("config", TypeNames.KtorfitxConfig)
		}
		return buildClassTypeSpec(classStructure.className) {
			addModifiers(KModifier.PRIVATE)
			addSuperinterface(classStructure.superinterface)
			primaryConstructor(primaryConstructorFunSpec)
			val ktorfitxConfigPropertySpec = buildPropertySpec("config", TypeNames.KtorfitxConfig, KModifier.PRIVATE) {
				initializer("config")
				mutable(false)
			}
			addProperty(ktorfitxConfigPropertySpec)
			addType(getCompanionObjectBuilder(classStructure))
			addFunctions(getFunSpecs(classStructure))
		}
	}
	
	/**
	 * 伴生对象
	 */
	private fun getCompanionObjectBuilder(classStructure: ClassStructure): TypeSpec {
		val typeName = classStructure.superinterface.copy(nullable = true)
		fileSpecBuilder.addImport(PackageNames.KTOR_UTILS_IO_LOCKS, "synchronized")
		return buildCompanionObjectTypeSpec {
			addModifiers(classStructure.kModifier)
			val optInSpec = buildAnnotationSpec(TypeNames.OptIn) {
				addMember("%T::class", TypeNames.InternalAPI)
			}
			addAnnotation(optInSpec)
			classStructure.apiStructure.apiScopeClassNames.forEach { apiScopeClassName ->
				val simpleName = apiScopeClassName.simpleNames.joinToString(".")
				val varName = simpleName.replaceFirstToLowercase()
				val instanceVarName = "${varName}Instance"
				val instancePropertySpec = buildPropertySpec(instanceVarName, typeName, KModifier.PRIVATE) {
					initializer("null")
					mutable(true)
				}
				addProperty(instancePropertySpec)
				val lockVarName = "${varName}SynchronizedObject"
				val mutexPropertySpec = buildPropertySpec(lockVarName, TypeNames.SynchronizedObject, KModifier.PRIVATE) {
					initializer("%T()", TypeNames.SynchronizedObject)
					mutable(false)
				}
				addProperty(mutexPropertySpec)
				val funSpec = buildFunSpec("getInstanceBy$simpleName") {
					addModifiers(classStructure.kModifier)
					returns(classStructure.superinterface)
					addParameter(
						"ktorfitx",
						TypeNames.Ktorfitx.parameterizedBy(apiScopeClassName)
					)
					val codeBlock = buildCodeBlock {
						beginControlFlow("return %N ?: synchronized(%N)", instanceVarName, lockVarName)
						addStatement("%N ?: %T(ktorfitx.config).also { %N = it }", instanceVarName, classStructure.className, instanceVarName)
						endControlFlow()
					}
					addCode(codeBlock)
				}
				addFunction(funSpec)
			}
		}
	}
	
	/**
	 * 扩展函数
	 */
	private fun getExpendPropertySpecs(classStructure: ClassStructure): List<PropertySpec> {
		val expendPropertyName = classStructure.superinterface.simpleName.replaceFirstChar { it.lowercase() }
		return classStructure.apiStructure.apiScopeClassNames.map { apiScopeClassName ->
			val simpleName = apiScopeClassName.simpleNames.joinToString(".")
			val jvmNameAnnotationSpec = buildAnnotationSpec(JvmName::class) {
				addMember("%S", "${expendPropertyName}By$simpleName")
			}
			val getterFunSpec = buildGetterFunSpec {
				addAnnotation(jvmNameAnnotationSpec)
				addStatement("return %T.%N(this)", classStructure.className, "getInstanceBy$simpleName")
			}
			buildPropertySpec(expendPropertyName, classStructure.superinterface, classStructure.kModifier) {
				receiver(TypeNames.Ktorfitx.parameterizedBy(apiScopeClassName))
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
				returns(returnStructure.typeName)
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
				with(WebSocketCodeBuilder(classStructure, funStructure, tokenVarName)) {
					buildCodeBlock()
				}
			} else {
				val isMockClient = funStructure.funModels.any { it is MockModel }
				val codeBlockKClass = if (isMockClient) MockClientCodeBlock::class else HttpClientCodeBlock::class
				with(HttpCodeBlockBuilder(classStructure, funStructure, codeBlockKClass, tokenVarName)) {
					buildCodeBlock()
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