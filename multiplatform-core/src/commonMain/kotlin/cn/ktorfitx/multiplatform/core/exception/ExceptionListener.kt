package cn.ktorfitx.multiplatform.core.exception

import kotlin.reflect.KFunction

interface ExceptionListener<in E : Exception, out R : Any> {
	
	fun KFunction<*>.onExceptionListener(e: E): R
}