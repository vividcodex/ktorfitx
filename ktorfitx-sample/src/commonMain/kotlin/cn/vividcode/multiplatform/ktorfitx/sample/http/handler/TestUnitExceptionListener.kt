package cn.vividcode.multiplatform.ktorfitx.sample.http.handler

import cn.vividcode.multiplatform.ktorfitx.api.exception.ExceptionListener
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import io.ktor.client.plugins.*
import kotlin.reflect.KFunction

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 15:06
 *
 * 文件介绍：TestExceptionHandler
 */
object TestUnitExceptionListener : ExceptionListener<TestException, Unit> {
	
	override fun KFunction<*>.onExceptionListener(e: TestException) {
		println(e.message!!)
	}
}

object TestResultBodyExceptionListener : ExceptionListener<ResponseException, ResultBody<String>> {
	
	override fun KFunction<*>.onExceptionListener(e: ResponseException): ResultBody<String> {
		println("??? : ${e.message}")
		return ResultBody.exception(e)
	}
}

class TestException : Exception("TestExceptionListener 异常测试")