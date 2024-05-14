package cn.vividcode.multiplatform.ktor.client.api.annotation

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/14 15:24
 *
 * 介绍：Form
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(val name: String = "")