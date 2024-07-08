package cn.vividcode.multiplatform.ktor.client.ksp.model.structure

import cn.vividcode.multiplatform.ktor.client.ksp.expends.classNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午11:04
 *
 * 介绍：ReturnStructure
 */
internal data class ReturnStructure(
	val typeName: TypeName
) {
	
	val classNames: List<ClassName> by lazy {
		when (typeName) {
			is ClassName -> listOf(typeName)
			is ParameterizedTypeName -> typeName.classNames
			else -> error("不支持的返回类型 $typeName")
		}
	}
}