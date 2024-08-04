package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/7 下午7:04
 *
 * 介绍：PUT
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PUT(
	val url: String,
	val auth: Boolean = false
)