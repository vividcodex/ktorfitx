package cn.vividcode.multiplatform.ktorfitx.annotation

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/14 22:17
 *
 * 文件介绍：PATCH
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PATCH(
	val url: String
)