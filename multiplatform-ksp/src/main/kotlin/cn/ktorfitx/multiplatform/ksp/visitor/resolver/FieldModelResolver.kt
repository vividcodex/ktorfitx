package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.FieldModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSFunctionDeclaration.resolveFieldModels(): List<FieldModel> {
	return this.parameters.mapNotNull { parameter ->
		val annotation = parameter.getKSAnnotationByType(TypeNames.Field) ?: return@mapNotNull null
		val varName = parameter.name!!.asString()
		var name = annotation.getValueOrNull<String>("name")
		if (name.isNullOrBlank()) {
			name = varName
		}
		val isString = parameter.type.toTypeName() == String::class.asTypeName()
		FieldModel(name, varName, isString)
	}
}