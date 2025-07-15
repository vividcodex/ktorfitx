package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.camelToHeaderCase
import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.HeaderModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object HeaderModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<HeaderModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(ClassNames.Header) ?: return@mapNotNull null
			var name = annotation.getValueOrNull<String>("name")
			val varName = valueParameter.name!!.asString()
			if (name.isNullOrBlank()) {
				name = varName.camelToHeaderCase()
			}
			HeaderModel(name, varName)
		}
	}
}