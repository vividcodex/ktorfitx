package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.QueryModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal fun KSFunctionDeclaration.resolveQueryModels(): List<QueryModel> {
	return this.parameters.mapNotNull { parameter ->
		val annotation = parameter.getKSAnnotationByType(ClassNames.Query) ?: return@mapNotNull null
		var name = annotation.getValueOrNull<String>("name")
		val varName = parameter.name!!.asString()
		if (name.isNullOrBlank()) {
			name = varName
		}
		QueryModel(name, varName)
	}
}