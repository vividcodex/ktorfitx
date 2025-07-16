package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.AttributeModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSFunctionDeclaration.resolveAttributeModels(): List<AttributeModel> {
	return this.parameters.mapNotNull { parameter ->
		val annotation = parameter.getKSAnnotationByType(ClassNames.Attribute) ?: return@mapNotNull null
		val varName = parameter.name!!.asString()
		var name = annotation.getValueOrNull<String>("name")
		if (name.isNullOrBlank()) {
			name = varName
		}
		val typeName = parameter.type.toTypeName()
		parameter.compileCheck(!typeName.isNullable) {
			"${simpleName.asString()} 函数的 $varName 参数不允许使用可空类型"
		}
		AttributeModel(name, varName, typeName)
	}
}