package cn.vividcode.multiplatform.ktor.client.api.encrypt

import korlibs.crypto.HasherFactory

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午10:14
 *
 * 介绍：EncryptType
 */
enum class EncryptType(
	val factory: HasherFactory,
) {
	MD4(korlibs.crypto.MD4),
	
	MD5(korlibs.crypto.MD5),
	
	SHA1(korlibs.crypto.SHA1),
	
	SHA256(korlibs.crypto.SHA256),
	
	SHA512(korlibs.crypto.SHA512),
}