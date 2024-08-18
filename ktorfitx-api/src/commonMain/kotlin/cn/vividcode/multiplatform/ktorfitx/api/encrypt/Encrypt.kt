package cn.vividcode.multiplatform.ktorfitx.api.encrypt

import cn.vividcode.multiplatform.ktorfitx.api.encrypt.HexType.Lower
import cn.vividcode.multiplatform.ktorfitx.api.encrypt.HexType.Upper
import korlibs.crypto.*
import korlibs.crypto.encoding.hexLower
import korlibs.crypto.encoding.hexUpper

/**
 * String 加密工具
 */
fun String.encrypt(encryptType: EncryptType, hexType: HexType, layer: Int): String {
	return (0 ..< layer).fold(this.encodeToByteArray()) { acc, _ ->
		encryptType.factory.digest(acc).bytes
	}.toHex(hexType)
}

private fun ByteArray.toHex(hexType: HexType): String {
	return when (hexType) {
		Upper -> this.hexUpper
		Lower -> this.hexLower
	}
}

private val EncryptType.factory: HasherFactory
	get() = when (this) {
		EncryptType.MD4 -> MD4
		EncryptType.MD5 -> MD5
		EncryptType.SHA1 -> SHA1
		EncryptType.SHA256 -> SHA256
		EncryptType.SHA512 -> SHA512
	}