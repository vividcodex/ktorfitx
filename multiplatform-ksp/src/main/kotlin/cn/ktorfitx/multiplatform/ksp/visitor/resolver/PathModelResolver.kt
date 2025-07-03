package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValue
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.PathModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object PathModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<PathModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(ClassNames.Path) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			var name = annotation.getValue<String?>("name")
			if (name.isNullOrBlank()) {
				name = varName
			}
			PathModel(name, varName, valueParameter)
		}
	}
}