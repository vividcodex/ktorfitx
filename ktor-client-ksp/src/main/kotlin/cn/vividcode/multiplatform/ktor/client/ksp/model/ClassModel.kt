package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/12 上午12:15
 *
 * 介绍：ClassModel
 */
internal data class ClassModel(
	val className: ClassName,
	val superinterface: ClassName,
	val functionModels: List<FunctionModel>
)