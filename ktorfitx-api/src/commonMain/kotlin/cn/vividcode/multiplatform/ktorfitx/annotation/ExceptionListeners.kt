package cn.vividcode.multiplatform.ktorfitx.annotation

import cn.vividcode.multiplatform.ktorfitx.api.exception.ExceptionListener
import kotlin.reflect.KClass

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 14:38
 *
 * 文件介绍：Exception
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ExceptionListeners(
	val listener: KClass<out ExceptionListener<*, *>>,
	vararg val listeners: KClass<out ExceptionListener<*, *>>
)