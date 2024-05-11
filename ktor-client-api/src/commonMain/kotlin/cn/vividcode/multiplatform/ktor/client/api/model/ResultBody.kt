package cn.vividcode.multiplatform.ktor.client.api.model

import kotlinx.serialization.Serializable

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/9 下午11:52
 *
 * 介绍：ResultBody
 */
@Serializable
data class ResultBody<T : Any>(
	val code: Int,
	val msg: String,
	val data: T? = null
) {
	
	companion object {
		
		/**
		 * 失败
		 */
		fun <T : Any> failure(code: Int, msg: String): ResultBody<T> {
			return ResultBody(code, msg, null)
		}
		
		/**
		 * 异常
		 */
		fun <T : Any> exception(e: Exception): ResultBody<T> {
			return ResultBody(-1, e.message ?: e.toString(), null)
		}
	}
}