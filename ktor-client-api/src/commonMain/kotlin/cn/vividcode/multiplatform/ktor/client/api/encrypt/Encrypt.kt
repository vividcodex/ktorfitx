package cn.vividcode.multiplatform.ktor.client.api.encrypt

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