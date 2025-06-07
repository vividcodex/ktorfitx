package cn.vividcode.multiplatform.ktorfitx.api.model

import kotlinx.serialization.Serializable

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/9 23:52
 *
 * 文件介绍：ResultBody
 */
@Serializable
data class ResultBody<out T : Any>(
	val code: Int,
	val msg: String,
	val data: T? = null
) {
	
	companion object {
		
		/**
		 * 成功
		 */
		fun <T : Any> success(data: T?, msg: String): ResultBody<T> {
			return ResultBody(0, msg, data)
		}
		
		/**
		 * 失败
		 */
		fun <T : Any> failure(code: Int, msg: String): ResultBody<T> {
			return ResultBody(code, msg)
		}
		
		/**
		 * 异常
		 */
		fun <T : Any> exception(e: Exception): ResultBody<T> {
			return ResultBody(-1, e.message ?: e.toString())
		}
	}
}