package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目名称：vividcode-multiplatform
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/4/3 15:40
 *
 * 文件介绍：SHA256
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Encrypt(
	val encryptType: EncryptType = EncryptType.SHA256,
	val layer: Int = 1
)