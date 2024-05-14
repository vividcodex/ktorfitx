package cn.vividcode.multiplatform.ktor.client.api.annotation

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/3/23 21:09
 *
 * 介绍：Query
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(val name: String = "")