package cn.vividcode.multiplatform.ktor.client.api.encrypt

import korlibs.crypto.HasherFactory
import korlibs.crypto.MD4 as K_MD4
import korlibs.crypto.MD5 as K_MD5
import korlibs.crypto.SHA1 as K_SHA1
import korlibs.crypto.SHA256 as K_SHA256
import korlibs.crypto.SHA512 as K_SHA512

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
	/**
	 * MD4
	 */
	MD4(K_MD4),
	
	/**
	 * MD5
	 */
	MD5(K_MD5),
	
	/**
	 * SHA1
	 */
	SHA1(K_SHA1),
	
	/**
	 * SHA256
	 */
	SHA256(K_SHA256),
	
	/**
	 * SHA512
	 */
	SHA512(K_SHA512),
}