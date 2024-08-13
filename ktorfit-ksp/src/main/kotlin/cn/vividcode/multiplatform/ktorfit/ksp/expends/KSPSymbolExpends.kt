package cn.vividcode.multiplatform.ktorfit.ksp.expends

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * 获取 KSAnnotation
 */
internal fun <T : Annotation> KSAnnotated.getKSAnnotationByType(annotationKClass: KClass<T>): KSAnnotation? {
	return this.annotations.filter {
		it.shortName.getShortName() == annotationKClass.simpleName &&
				it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
	}.firstOrNull()
}

/**
 * 获取 KSAnnotation
 */
internal fun KSAnnotated.getKSAnnotationByType(annotationClassName: ClassName): KSAnnotation? {
	return this.annotations.filter {
		it.shortName.getShortName() == annotationClassName.simpleName &&
				it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationClassName.canonicalName
	}.firstOrNull()
}

/**
 * 直接获取注解的对象，不支持 KClass
 */
@OptIn(KspExperimental::class)
internal fun <T : Annotation> KSAnnotated.getAnnotationByType(annotationKClass: KClass<T>): T? {
	return this.getAnnotationsByType(annotationKClass).firstOrNull()
}

/**
 * 获取注解上的数据
 */
internal fun <V> KSAnnotation.getArgumentValue(property: KProperty1<*, V>): V? {
	return this.getArgumentValue(property.name)
}

/**
 * 获取注解上的数据
 */
@Suppress("UNCHECKED_CAST")
internal fun <V> KSAnnotation.getArgumentValue(propertyName: String): V? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value
	check(value !is KSType) { "value 是 KSType 类型的！" }
	check(value !is ArrayList<*>) { "value 是 ArrayList<*> 类型的" }
	return value as? V
}

/**
 * 获取注解上的数组数据
 */
internal inline fun <reified T : Any> KSAnnotation.getArgumentArrayValue(property: KProperty1<*, *>): Array<T>? {
	return this.getArgumentArrayValue(property.name)
}

/**
 * 获取注解上的数组数据
 */
internal inline fun <reified T : Any> KSAnnotation.getArgumentArrayValue(propertyName: String): Array<T>? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value
	return if (value is ArrayList<*>) {
		return value.toTypedArray().map {
			it as T
		}.toTypedArray()
	} else null
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
internal fun <T : Annotation> KSAnnotation.getArgumentClassName(property: KProperty1<T, KClass<*>>): ClassName? {
	return this.getArgumentClassName(property.name)
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
internal fun KSAnnotation.getArgumentClassName(propertyName: String): ClassName? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value ?: return null
	check(value is KSType) { "value is not KSType!" }
	return (value.declaration as KSClassDeclaration).toClassName()
}

/**
 * 获取TypeName上用到的所有ClassName
 */
internal val TypeName.classNames: List<ClassName>
	get() = when (this) {
		is ClassName -> listOf(this)
		is ParameterizedTypeName -> this.classNames
		else -> error("不支持的类型 $simpleName")
	}

private val ParameterizedTypeName.classNames: List<ClassName>
	get() = buildList {
		this += rawType
		typeArguments.forEach {
			if (it is ClassName) {
				this += it
			} else if (it is ParameterizedTypeName) {
				this += it.classNames
			}
		}
	}

/**
 * 获取 TypeName 上的 simpleName
 */
internal val TypeName.simpleName: String
	get() = when (this) {
		is ParameterizedTypeName -> this.simpleName
		is ClassName -> this.simpleName
		else -> this.toString()
	}

private val ParameterizedTypeName.simpleName: String
	get() = buildString {
		append(rawType.simpleName)
		if (typeArguments.isNotEmpty()) {
			append("<")
			val code = typeArguments.joinToString {
				when (it) {
					is ClassName -> it.simpleName
					is ParameterizedTypeName -> it.simpleName
					else -> it.toString()
				}
			}
			append(code)
			append(">")
		}
	}

/**
 * 获取 TypeName 的 rawType
 */
internal val TypeName.rawType: TypeName
	get() = when (this) {
		is ParameterizedTypeName -> this.rawType
		else -> this
	}