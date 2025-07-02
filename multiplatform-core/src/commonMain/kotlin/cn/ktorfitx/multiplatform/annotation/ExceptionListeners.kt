package cn.ktorfitx.multiplatform.annotation

import cn.ktorfitx.multiplatform.core.exception.ExceptionListener
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ExceptionListeners(
	val listener: KClass<out ExceptionListener<*, *>>,
	vararg val listeners: KClass<out ExceptionListener<*, *>>
)