package cn.vividcode.multiplatform.ktorfitx.ksp.model.structure

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.classNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 23:04
 *
 * 文件介绍：ReturnStructure
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