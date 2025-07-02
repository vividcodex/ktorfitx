package cn.ktorfitx.multiplatform.sample.http.listener

import cn.ktorfitx.multiplatform.core.exception.ExceptionListener
import kotlin.reflect.KFunction

/**
 * TestUnitExceptionListener
 */
data object TestUnitExceptionListener : ExceptionListener<TestException, Unit> {
	
	override fun KFunction<*>.onExceptionListener(e: TestException) {
	
	}
}

/**
 * TestStringExceptionListener
 */
object TestStringExceptionListener : ExceptionListener<Exception, String> {
	
	override fun KFunction<*>.onExceptionListener(e: Exception): String {
		return e.toString()
	}
}

/**
 * TestException
 */
class TestException : Exception("TestExceptionListener 异常测试")