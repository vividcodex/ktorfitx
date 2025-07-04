package cn.ktorfitx.common.ksp.util.expends

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * 是否包含注解
 */
fun KSAnnotated.hasAnnotation(annotationClassName: ClassName): Boolean {
	return this.annotations.any {
		it.shortName.getShortName() == annotationClassName.simpleName &&
			it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationClassName.canonicalName
	}
}

/**
 * 获取 KSAnnotation
 */
fun KSAnnotated.getKSAnnotationByType(annotationClassName: ClassName): KSAnnotation? {
	return this.annotations.filter {
		it.shortName.getShortName() == annotationClassName.simpleName &&
			it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationClassName.canonicalName
	}.firstOrNull()
}

/**
 * 通过 KSAnnotation 获取参数的 KSClassDeclaration
 */
fun KSAnnotation.getArgumentKSClassDeclaration(propertyName: String): KSClassDeclaration? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value
	check(value is KSType) { "$value is not KSType!" }
	return value.declaration as KSClassDeclaration
}

/**
 * 获取注解上的数据
 */
@Suppress("UNCHECKED_CAST")
fun <V> KSAnnotation.getValue(propertyName: String): V? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value
	check(value !is KSType && value !is ArrayList<*>) {
		"此方法不支持此类型"
	}
	return value as? V
}

/**
 * 获取注解上的数组数据
 */
inline fun <reified T : Any> KSAnnotation.getValues(propertyName: String): Array<T>? {
	val values = this.arguments.find { it.name?.asString() == propertyName }?.value
	return if (values is ArrayList<*>) {
		values.map { it as T }.toTypedArray()
	} else null
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
fun KSAnnotation.getClassName(propertyName: String): ClassName? {
	val value = this.arguments.find { it.name?.asString() == propertyName }?.value ?: return null
	return when (value) {
		is KSClassDeclaration -> value.toClassName()
		is KSType -> (value.declaration as KSClassDeclaration).toClassName()
		else -> error("$value is not a KSClassDeclaration or KSType")
	}
}

/**
 * 获取注解上的 KClass 的 ClassName
 */
fun KSAnnotation.getClassNames(propertyName: String): Array<ClassName>? {
	val values = this.arguments.find { it.name?.asString() == propertyName }?.value
	return if (values is ArrayList<*>) {
		values.map {
			check(it is KSType) { "$it is not KSType!" }
			(it.declaration as KSClassDeclaration).toClassName()
		}.toTypedArray()
	} else null
}

@Suppress("UNCHECKED_CAST", "unused")
fun <T> Any.safeAs(): T = this as T

fun KSFunctionDeclaration.isExtension(className: ClassName): Boolean {
	return this.extensionReceiver?.resolve()?.declaration?.qualifiedName?.asString() == className.canonicalName
}