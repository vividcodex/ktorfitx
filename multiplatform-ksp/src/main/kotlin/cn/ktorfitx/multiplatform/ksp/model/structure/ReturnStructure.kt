package cn.ktorfitx.multiplatform.ksp.model.structure

import cn.ktorfitx.multiplatform.ksp.expends.classNames
import cn.ktorfitx.multiplatform.ksp.expends.rawType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 23:04
 *
 * 文件介绍：ReturnStructure
 */
internal class ReturnStructure(
	val typeName: TypeName,
) {
	
	val classNames: List<ClassName> by lazy {
		when (typeName) {
			is ClassName -> listOf(typeName)
			is ParameterizedTypeName -> typeName.classNames
			else -> error("不支持的返回类型 $typeName")
		}
	}
	
	val rawType by lazy { this.typeName.rawType }
	
	val notNullRawType by lazy {
		if (rawType.isNullable) {
			this.rawType.copy(nullable = false)
		} else this.rawType
	}
	
	val isNullable by lazy { this.typeName.isNullable }
}