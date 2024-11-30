package cn.vividcode.multiplatform.ktorfitx.annotation

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 21:04
 *
 * 文件介绍：POST
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class POST(
	val url: String
)