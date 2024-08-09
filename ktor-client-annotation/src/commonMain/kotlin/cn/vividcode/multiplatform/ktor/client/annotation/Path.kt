package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目名称：vividcode-multiplatform
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/5/14 15:24
 *
 * 文件介绍：Path
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(val name: String = "")