package cn.vividcode.multiplatform.ktorfitx.sample.http.listener

import cn.vividcode.multiplatform.ktorfitx.api.exception.ExceptionListener
import kotlin.reflect.KFunction

/**
 * TestUnitExceptionListener
 */
data object TestUnitExceptionListener : ExceptionListener<TestException, Unit> {
	
	override fun KFunction<*>.onExceptionListener(e: TestException) {
	
	}
}

/**
 * TestResultBodyExceptionListener
 */
object TestResultBodyExceptionListener : ExceptionListener<Exception, Int> {
	
	override fun KFunction<*>.onExceptionListener(e: Exception): Int {
		return 1
	}
}

/**
 * TestException
 */
class TestException : Exception("TestExceptionListener 异常测试")