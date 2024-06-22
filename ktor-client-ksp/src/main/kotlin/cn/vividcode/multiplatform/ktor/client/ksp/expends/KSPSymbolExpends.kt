package cn.vividcode.multiplatform.ktor.client.ksp.expends

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

private val ksAnnotationCacheMap = mutableMapOf<KSAnnotated, MutableMap<KClass<out Annotation>, KSAnnotation?>>()

/**
 * 查找注解
 */
internal fun KSAnnotated.getAnnotation(kClass: KClass<out Annotation>): KSAnnotation? {
	val cacheMap = ksAnnotationCacheMap.getOrPut(this) { mutableMapOf() }
	return cacheMap.getOrPut(kClass) {
		this.annotations.firstOrNull {
			it.annotationType.resolve().declaration.qualifiedName?.asString() == kClass.qualifiedName
		}
	}
}

/**
 * 是否包含注解
 */
internal fun KSAnnotated.hasAnnotation(kClass: KClass<out Annotation>): Boolean {
	return getAnnotation(kClass) != null
}

/**
 * 获取注解存在数量
 */
internal fun KSAnnotated.getAnnotationSize(vararg kClasses: KClass<out Annotation>): Int {
	return kClasses.mapNotNull { this.getAnnotation(it) }.size
}

/**
 * 查找注解上的字段
 */
internal fun <V> KSAnnotation.getArgumentValue(kProperty1: KProperty1<out Annotation, V>): V? {
	return this.getArgumentValue(kProperty1.name)
}

private val argumentValueCacheMap = mutableMapOf<KSAnnotation, MutableMap<String, Any?>>()

/**
 * 查找注解上的字段
 */
@Suppress("UNCHECKED_CAST")
internal fun <V> KSAnnotation.getArgumentValue(name: String): V? {
	val cacheMap = argumentValueCacheMap.getOrPut(this) { mutableMapOf() }
	return cacheMap.getOrPut(name) {
		val value = this.arguments.find { it.name?.asString() == name }?.value
		if (value is ArrayList<*>) {
			value.toTypedArray()
		} else value
	} as? V
}

/**
 * ClassName
 */
internal val KSDeclaration.className: ClassName
	get() = ClassName(this.packageName.asString(), this.simpleName.asString())