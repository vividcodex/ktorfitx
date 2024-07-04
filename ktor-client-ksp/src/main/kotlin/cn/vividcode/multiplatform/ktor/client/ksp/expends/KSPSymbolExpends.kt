package cn.vividcode.multiplatform.ktor.client.ksp.expends

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
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
 * 直接获取注解的对象，不支持 KClass
 */
@OptIn(KspExperimental::class)
internal fun <T : Annotation> KSAnnotated.getAnnotationByType(annotationKClass: KClass<T>): T? {
	return this.getAnnotationsByType(annotationKClass).firstOrNull()
}

/**
 * 获取注解上的数据
 */
@Suppress("UNCHECKED_CAST")
internal fun <T, V> KSAnnotation.getArgumentValue(kProperty1: KProperty1<T, V>): V? {
	var value = this.arguments.find { it.name?.asString() == kProperty1.name }?.value
	check(value !is KSType)
	if (value is ArrayList<*>) {
		value = value.toTypedArray()
	}
	return value as? V
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
internal fun <T : Annotation> KSAnnotation.getArgumentClassName(kProperty1: KProperty1<T, KClass<*>>): ClassName? {
	val value = this.arguments.find { it.name?.asString() == kProperty1.name }?.value ?: return null
	check(value is KSType)
	return (value.declaration as KSClassDeclaration).toClassName()
}