package cn.ktorfitx.multiplatform.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResult<out T : Any>(
	val code: Int,
	val msg: String,
	val data: T? = null
) {
	
	companion object {
		
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