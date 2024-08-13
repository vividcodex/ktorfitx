@file:Suppress("UnusedReceiverParameter")

package cn.vividcode.multiplatform.ktorfit.api.encrypt

import cn.vividcode.multiplatform.ktorfit.api.mock.MockRequestBuilder
import io.ktor.client.request.*
import korlibs.crypto.*

fun HttpRequestBuilder.encrypt(content: Any, encryptType: EncryptType, layer: Int): String {
	return content.encrypt(encryptType, layer)
}

fun MockRequestBuilder.encrypt(content: Any, encryptType: EncryptType, layer: Int): String {
	return content.encrypt(encryptType, layer)
}

private fun Any.encrypt(encryptType: EncryptType, layer: Int): String {
	var hash = when (this) {
		is String -> this.encodeToByteArray()
		is ByteArray -> this
		else -> this.toString().encodeToByteArray()
	}.let { encryptType.factory.digest(it) }
	repeat(layer - 1) {
		hash = encryptType.factory.digest(hash.bytes)
	}
	return hash.hexLower
}

/**
 * factory
 */
private val EncryptType.factory: HasherFactory
	get() = when (this) {
		EncryptType.MD4 -> MD4
		EncryptType.MD5 -> MD5
		EncryptType.SHA1 -> SHA1
		EncryptType.SHA256 -> SHA256
		EncryptType.SHA512 -> SHA512
	}