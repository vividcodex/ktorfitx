package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValue
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.FieldModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object FieldModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<FieldModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(ClassNames.Field) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			var name = annotation.getValue<String?>("name")
			if (name.isNullOrBlank()) {
				name = varName
			}
			val isString = valueParameter.type.toTypeName() == String::class.asTypeName()
			FieldModel(name, varName, isString)
		}
	}
}