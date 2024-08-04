package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/3/23 20:44
 *
 * 介绍：GET
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GET(
	val url: String,
	val auth: Boolean = false
)