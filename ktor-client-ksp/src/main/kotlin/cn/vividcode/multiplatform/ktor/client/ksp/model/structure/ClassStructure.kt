package cn.vividcode.multiplatform.ktor.client.ksp.model.structure

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午3:47
 *
 * 介绍：ClassStructure
 */
internal data class ClassStructure(
	val className: ClassName,
	val superinterface: ClassName,
	val apiStructure: ApiStructure,
	val funStructures: Sequence<FunStructure>
)