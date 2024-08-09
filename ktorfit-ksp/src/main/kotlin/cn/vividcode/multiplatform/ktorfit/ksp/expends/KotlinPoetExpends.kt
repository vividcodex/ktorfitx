package cn.vividcode.multiplatform.ktorfit.ksp.expends

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

/**
 * buildFileSpec
 */
internal fun buildFileSpec(
	className: ClassName,
	block: FileSpec.Builder.() -> Unit = {}
): FileSpec = FileSpec.builder(className).apply(block).build()

/**
 * buildClassTypeSpec
 */
internal fun buildClassTypeSpec(
	className: ClassName,
	block: TypeSpec.Builder.() -> Unit = {}
): TypeSpec = TypeSpec.classBuilder(className).apply(block).build()

/**
 * buildCompanionObjectTypeSpec
 */
internal fun buildCompanionObjectTypeSpec(
	block: TypeSpec.Builder.() -> Unit = {}
): TypeSpec = TypeSpec.companionObjectBuilder().apply(block).build()

/**
 * buildFunSpec
 */
internal fun buildFunSpec(
	name: String,
	block: FunSpec.Builder.() -> Unit = {}
): FunSpec = FunSpec.builder(name).apply(block).build()

/**
 * buildConstructorFunSpec
 */
internal fun buildConstructorFunSpec(
	block: FunSpec.Builder.() -> Unit = {}
): FunSpec = FunSpec.constructorBuilder().apply(block).build()

/**
 * buildGetterFunSpec
 */
internal fun buildGetterFunSpec(
	block: FunSpec.Builder.() -> Unit = {}
): FunSpec = FunSpec.getterBuilder().apply(block).build()

/**
 * buildPropertySpec
 */
internal fun buildPropertySpec(
	name: String,
	type: KClass<*>,
	vararg modifiers: KModifier,
	block: PropertySpec.Builder.() -> Unit = {}
): PropertySpec = PropertySpec.builder(name, type, *modifiers).apply(block).build()

/**
 * buildPropertySpec
 */
internal fun buildPropertySpec(
	name: String,
	type: TypeName,
	vararg modifiers: KModifier,
	block: PropertySpec.Builder.() -> Unit = {}
): PropertySpec = PropertySpec.builder(name, type, *modifiers).apply(block).build()

/**
 * buildParameterSpec
 */
internal fun buildParameterSpec(
	name: String,
	type: TypeName,
	block: ParameterSpec.Builder.() -> Unit = {}
): ParameterSpec = ParameterSpec.builder(name, type).apply(block).build()