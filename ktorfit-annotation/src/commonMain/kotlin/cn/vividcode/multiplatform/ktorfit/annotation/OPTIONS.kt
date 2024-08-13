package cn.vividcode.multiplatform.ktorfit.annotation

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/14 22:01
 *
 * 文件介绍：OPTIONS
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OPTIONS(
	val url: String
)