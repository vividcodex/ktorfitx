package cn.ktorfitx.multiplatform.core.expends

import cn.ktorfitx.multiplatform.core.model.ApiResult
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * 获取 ApiResult，如果失败返回 ApiResult.failure()
 */
suspend inline fun <reified T : Any> HttpResponse.safeApiResult(): ApiResult<T> {
	return if (this.status.isSuccess()) {
		this.body()
	} else {
		ApiResult.failure(this.status.value, this.status.description)
	}
}

/**
 * 获取 ApiResult，如果失败返回 null
 */
suspend inline fun <reified T : Any> HttpResponse.safeApiResultOrNull(): ApiResult<T>? {
	return if (this.status.isSuccess()) {
		this.body()
	} else null
}

/**
 * 获取 ByteArray，如果失败返回 EmptyByteArray
 */
suspend fun HttpResponse.safeByteArray(): ByteArray {
	return if (this.status.isSuccess()) {
		this.readRawBytes()
	} else ByteArray(0)
}

/**
 * 获取 ByteArray，如果失败返回 null
 */
suspend fun HttpResponse.safeByteArrayOrNull(): ByteArray? {
	return if (this.status.isSuccess()) {
		this.readRawBytes()
	} else null
}

/**
 * 获取 String，如果失败返回 ""
 */
suspend fun HttpResponse.safeText(): String {
	return if (this.status.isSuccess()) {
		this.bodyAsText()
	} else ""
}

/**
 * 获取 String，如果失败返回 null
 */
suspend fun HttpResponse.safeTextOrNull(): String? {
	return if (this.status.isSuccess()) {
		this.bodyAsText()
	} else null
}