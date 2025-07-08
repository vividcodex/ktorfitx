package cn.ktorfitx.multiplatform.ksp.model.structure

import cn.ktorfitx.common.ksp.util.expends.classNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

internal class ReturnStructure(
	val typeName: TypeName,
) {
	
	val allClassNames: List<ClassName> by lazy {
		when (typeName) {
			is ClassName -> listOf(typeName)
			is ParameterizedTypeName -> typeName.classNames
			else -> error("不支持的返回类型 $typeName")
		}
	}
}