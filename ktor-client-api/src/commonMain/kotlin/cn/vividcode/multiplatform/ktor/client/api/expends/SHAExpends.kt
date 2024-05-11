package cn.vividcode.multiplatform.ktor.client.api.expends

import korlibs.crypto.SHA256

/**
 * sha256
 */
fun String.sha256(layer: Int): String {
	if (layer <= 0) return this
	var hash = SHA256.digest(this.encodeToByteArray())
	for (i in 0 .. layer - 2) {
		hash = SHA256.digest(hash.bytes)
	}
	return hash.hexLower
}