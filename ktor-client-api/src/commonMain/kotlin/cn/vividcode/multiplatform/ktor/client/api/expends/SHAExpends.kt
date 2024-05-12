package cn.vividcode.multiplatform.ktor.client.api.expends

import korlibs.crypto.SHA256

/**
 * sha256
 */
fun <T> T.sha256(layer: Int): String {
	if (this !is String || layer <= 0) {
		return this.toString()
	}
	var hash = SHA256.digest(this.encodeToByteArray())
	for (i in 0 .. layer - 2) {
		hash = SHA256.digest(hash.bytes)
	}
	return hash.hexLower
}