package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.camelToHeaderCase
import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.HeaderModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal fun KSFunctionDeclaration.resolveHeaderModels(): List<HeaderModel> {
	return this.parameters.mapNotNull { parameter ->
		val annotation = parameter.getKSAnnotationByType(TypeNames.Header) ?: return@mapNotNull null
		var name = annotation.getValueOrNull<String>("name")
		val varName = parameter.name!!.asString()
		if (name.isNullOrBlank()) {
			name = varName.camelToHeaderCase()
		}
		HeaderModel(name, varName)
	}
}