package cn.ktorfitx.multiplatform.ksp.model.structure

import cn.ktorfitx.common.ksp.util.expends.classNames
import cn.ktorfitx.common.ksp.util.expends.rawType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

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