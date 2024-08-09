package cn.vividcode.multiplatform.ktor.client.ksp.model.structure

import com.squareup.kotlinpoet.ClassName

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 14:15
 *
 * 文件介绍：ApiStructure
 */
internal data class ApiStructure(
	val url: String,
	val apiScopeClassName: ClassName
)