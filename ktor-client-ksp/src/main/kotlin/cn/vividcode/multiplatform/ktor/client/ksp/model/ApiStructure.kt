package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午2:15
 *
 * 介绍：ApiStructure
 */
internal data class ApiStructure(
	val url: String,
	val apiScopeClassName: ClassName
)