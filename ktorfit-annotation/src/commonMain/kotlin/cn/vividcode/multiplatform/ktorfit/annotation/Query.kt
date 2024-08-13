package cn.vividcode.multiplatform.ktorfit.annotation

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 21:09
 *
 * 文件介绍：Query
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(
	val name: String = ""
)