package cn.vividcode.multiplatform.ktorfitx.api.exception

import kotlin.reflect.KFunction

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 14:40
 *
 * 文件介绍：ExceptionListener
 */
interface ExceptionListener<in E : Exception, out R : Any> {
	
	fun KFunction<*>.onExceptionListener(e: E): R
}