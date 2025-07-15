package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.PathModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object PathModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<PathModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Path) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			var name = annotation.getValueOrNull<String>("name")
			if (name.isNullOrBlank()) {
				name = varName
			}
			PathModel(name, varName, parameter)
		}
	}
}