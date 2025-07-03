package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValue
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.PartModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object PartModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<PartModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(ClassNames.Part) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			var name = annotation.getValue<String?>("name")
			if (name.isNullOrBlank()) {
				name = varName
			}
			PartModel(name, varName)
		}
	}
}