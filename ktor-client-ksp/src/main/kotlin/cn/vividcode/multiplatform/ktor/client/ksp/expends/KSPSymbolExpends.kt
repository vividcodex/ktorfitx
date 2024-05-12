package cn.vividcode.multiplatform.ktor.client.ksp.expends

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * 查找注解
 */
fun KSAnnotated.getAnnotation(kClass: KClass<*>): KSAnnotation? = this.annotations.find {
	it.annotationType.resolve().declaration.qualifiedName?.asString() == kClass.qualifiedName
}

/**
 * 获取注解存在数量
 */
fun KSAnnotated.getAnnotationSize(vararg kClasses: KClass<*>): Int {
	return kClasses.mapNotNull { this.getAnnotation(it) }.size
}

/**
 * 查找注解上的字段
 */
fun <V> KSAnnotation.getArgumentValue(kProperty1: KProperty1<*, V>): V? {
	return this.getArgumentValue(kProperty1.name)
}

/**
 * 查找注解上的字段
 */
@Suppress("UNCHECKED_CAST")
fun <V> KSAnnotation.getArgumentValue(name: String): V? {
	val value = this.arguments.find { it.name?.asString() == name }?.value
	return if (value is ArrayList<*>) {
		value.map { it.toString() }.toTypedArray() as V
	} else {
		value as? V
	}
}

/**
 * ClassName
 */
val KSDeclaration.className: ClassName
	get() = ClassName(this.packageName.asString(), this.simpleName.asString())