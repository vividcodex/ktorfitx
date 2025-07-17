package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.PartModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal fun KSFunctionDeclaration.resolvePartModels(): List<PartModel> {
	return this.parameters.mapNotNull { parameter ->
		val annotation = parameter.getKSAnnotationByType(TypeNames.Part) ?: return@mapNotNull null
		val varName = parameter.name!!.asString()
		var name = annotation.getValueOrNull<String>("name")
		if (name.isNullOrBlank()) {
			name = varName
		}
		PartModel(name, varName)
	}
}