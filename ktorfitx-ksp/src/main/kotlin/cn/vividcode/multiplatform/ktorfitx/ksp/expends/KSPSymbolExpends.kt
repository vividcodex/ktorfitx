package cn.vividcode.multiplatform.ktorfitx.ksp.expends

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
 * 通过 KSAnnotation 获取参数的 KSClassDeclaration
 */
internal fun KSAnnotation.getArgumentKSClassDeclaration(propertyName: String): KSClassDeclaration? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value
	check(value is KSType) { "$value is not KSType!" }
	return value.declaration as KSClassDeclaration
}

/**
 * 获取注解上的数据
 */
internal fun <V> KSAnnotation.getValue(property: KProperty1<*, V>): V? {
	return this.getValue(property.name)
}

/**
 * 获取注解上的数据
 */
@Suppress("UNCHECKED_CAST")
internal fun <V> KSAnnotation.getValue(propertyName: String): V? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value
	check(value !is KSType && value !is ArrayList<*>) {
		"此方法不支持此类型"
	}
	return value as? V
}

/**
 * 获取注解上的数组数据
 */
internal inline fun <reified T : Any> KSAnnotation.getValues(propertyName: String): Array<T>? {
	val values = this.arguments.find { it.name?.asString() == propertyName }?.value
	return if (values is ArrayList<*>) {
		values.map { it as T }.toTypedArray()
	} else null
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
internal fun KSAnnotation.getClassName(propertyName: String): ClassName? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value ?: return null
	check(value is KSType) { "$value is not KSType!" }
	return (value.declaration as KSClassDeclaration).toClassName()
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
internal fun KSAnnotation.getClassNames(propertyName: String): Array<ClassName>? {
	val values = this.arguments.find { it.name?.asString() == propertyName }?.value
	return if (values is ArrayList<*>) {
		values.map {
			check(it is KSType) { "$it is not KSType!" }
			(it.declaration as KSClassDeclaration).toClassName()
		}.toTypedArray()
	} else null
}

@Suppress("UNCHECKED_CAST", "unused")
internal fun <T> Any.safeAs(): T = this as T

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
internal val TypeName.rawType: ClassName
	get() = when (this) {
		is ParameterizedTypeName -> {
			this.rawType.copy(this.isNullable, emptyList(), emptyMap())
		}
		
		is ClassName -> this
		else -> error("不允许使用 rawType 属性")
	}