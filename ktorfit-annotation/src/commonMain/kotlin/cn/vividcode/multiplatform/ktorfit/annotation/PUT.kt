package cn.vividcode.multiplatform.ktorfit.annotation

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/7 19:04
 *
 * 文件介绍：PUT
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PUT(
	val url: String
)