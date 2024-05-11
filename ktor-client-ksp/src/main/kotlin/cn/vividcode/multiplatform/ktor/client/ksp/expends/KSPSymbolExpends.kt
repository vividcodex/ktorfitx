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
	var size = 0
	kClasses.forEach {
		val annotation = this.getAnnotation(it)
		if (annotation != null) {
			size++
		}
	}
	return size
}

/**
 * 查找注解上的字段
 */
@Suppress("UNCHECKED_CAST")
fun <V> KSAnnotation.getArgumentValue(kProperty1: KProperty1<*, V>): V? {
	return this.arguments.find { it.name?.asString() == kProperty1.name }?.value as? V
}

/**
 * ClassName
 */
val KSDeclaration.className: ClassName
	get() = ClassName(this.packageName.asString(), this.simpleName.asString())