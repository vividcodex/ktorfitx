package cn.vividcode.multiplatform.ktorfitx.annotation

import cn.vividcode.multiplatform.ktorfitx.core.exception.ExceptionListener
import kotlin.reflect.KClass

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 14:38
 *
 * 文件介绍：ExceptionListeners
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ExceptionListeners(
	val listener: KClass<out ExceptionListener<*, *>>,
	vararg val listeners: KClass<out ExceptionListener<*, *>>
)