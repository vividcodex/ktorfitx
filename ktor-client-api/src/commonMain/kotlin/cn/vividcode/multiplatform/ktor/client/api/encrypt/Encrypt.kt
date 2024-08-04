package cn.vividcode.multiplatform.ktor.client.api.encrypt

import cn.vividcode.multiplatform.ktor.client.annotation.EncryptType
import korlibs.crypto.*

/**
 * encrypt String
 */
fun String.encrypt(encryptType: EncryptType, layer: Int): String {
	return this.encodeToByteArray().encrypt(encryptType, layer)
}

/**
 * encrypt ByteArray
 */
fun ByteArray.encrypt(encryptType: EncryptType, layer: Int): String {
	var hash = encryptType.factory.digest(this)
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