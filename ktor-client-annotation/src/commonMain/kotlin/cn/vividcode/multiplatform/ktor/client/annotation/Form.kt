package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/3/23 21:21
 *
 * 介绍：Form
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Form(val name: String = "")