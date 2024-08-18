package cn.vividcode.multiplatform.ktorfitx.api.encrypt

import korlibs.crypto.*
import korlibs.crypto.encoding.hexLower
import korlibs.crypto.encoding.hexUpper

/**
 * String 加密工具
 */
fun String.encrypt(encryptType: EncryptType, hexType: HexType, layer: Int): String {
	var bytes = this.encodeToByteArray()
	repeat(layer) {
		bytes = encryptType.factory.digest(bytes).bytes
	}
	return bytes.toHex(hexType)
}

private fun ByteArray.toHex(hexType: HexType): String = when (hexType) {
	HexType.Upper -> this.hexUpper
	HexType.Lower -> this.hexLower
}

private val EncryptType.factory: HasherFactory
	get() = when (this) {
		EncryptType.MD4 -> MD4
		EncryptType.MD5 -> MD5
		EncryptType.SHA1 -> SHA1
		EncryptType.SHA256 -> SHA256
		EncryptType.SHA512 -> SHA512
	}