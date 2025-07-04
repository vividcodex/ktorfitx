package cn.vividcode.multiplatform.ktorfitx.annotation

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/3/23 21:21
 *
 * 文件介绍：Part
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Part(
	val name: String = ""
)