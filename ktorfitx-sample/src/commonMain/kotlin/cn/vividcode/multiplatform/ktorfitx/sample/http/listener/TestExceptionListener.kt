package cn.vividcode.multiplatform.ktorfitx.sample.http.listener

import cn.vividcode.multiplatform.ktorfitx.api.exception.ExceptionListener
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import kotlin.reflect.KFunction

/**
 * TestUnitExceptionListener
 */
object TestUnitExceptionListener : ExceptionListener<TestException, Unit> {
	
	override fun KFunction<*>.onExceptionListener(e: TestException) {
	
	}
}

/**
 * TestResultBodyExceptionListener
 */
object TestResultBodyExceptionListener : ExceptionListener<Exception, ResultBody<String>> {
	
	override fun KFunction<*>.onExceptionListener(e: Exception): ResultBody<String> {
		return ResultBody.exception(e)
	}
}

/**
 * TestException
 */
class TestException : Exception("TestExceptionListener 异常测试")