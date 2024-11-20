package cn.vividcode.multiplatform.ktorfitx.api.expends

import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * 获取 ResultBody，如果失败返回 ResultBody.failure()
 */
suspend inline fun <reified T : Any> HttpResponse.safeResultBody(): ResultBody<T> {
	return if (this.status.isSuccess()) {
		this.body()
	} else {
		ResultBody.failure(this.status.value, this.status.description)
	}
}

/**
 * 获取 ResultBody，如果失败返回 null
 */
suspend inline fun <reified T : Any> HttpResponse.safeResultBodyOrNull(): ResultBody<T>? {
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
	} else EmptyByteArray
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

val EmptyByteArray by lazy { ByteArray(0) }