package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValue
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.HeaderModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object HeaderModelResolver {
	
	private val regex = "([a-z])([A-Z])".toRegex()
	
	fun KSFunctionDeclaration.resolves(): List<HeaderModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(ClassNames.Header) ?: return@mapNotNull null
			var name = annotation.getValue<String?>("name")
			val varName = valueParameter.name!!.asString()
			if (name.isNullOrBlank()) {
				name = varName.replace(regex) {
					"${it.groupValues[1]}-${it.groupValues[2]}"
				}.replaceFirstChar { it.uppercase() }
			}
			HeaderModel(name, varName)
		}
	}
}