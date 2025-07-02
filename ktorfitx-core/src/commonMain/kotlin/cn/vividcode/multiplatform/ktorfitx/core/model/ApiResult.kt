package cn.vividcode.multiplatform.ktorfitx.core.model

import kotlinx.serialization.Serializable

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/9 23:52
 *
 * 文件介绍：ApiResult
 */
@Serializable
data class ApiResult<out T : Any>(
	val code: Int,
	val msg: String,
	val data: T? = null
) {
	
	companion object Companion {
		
		/**
		 * 成功
		 */
		fun <T : Any> success(data: T?, msg: String): ApiResult<T> {
			return ApiResult(0, msg, data)
		}
		
		/**
		 * 失败
		 */
		fun <T : Any> failure(code: Int, msg: String): ApiResult<T> {
			return ApiResult(code, msg)
		}
		
		/**
		 * 异常
		 */
		fun <T : Any> exception(e: Exception): ApiResult<T> {
			return ApiResult(-1, e.message ?: e.toString())
		}
	}
}