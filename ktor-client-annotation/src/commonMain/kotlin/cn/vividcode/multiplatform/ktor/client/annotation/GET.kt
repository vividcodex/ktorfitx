package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目名称：vividcode-multiplatform
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 20:44
 *
 * 文件介绍：GET
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GET(
	val url: String,
	val auth: Boolean = false
)