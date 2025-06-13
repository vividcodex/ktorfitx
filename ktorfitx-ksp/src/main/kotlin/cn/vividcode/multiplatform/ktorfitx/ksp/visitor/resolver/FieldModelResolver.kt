package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Field
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.FieldModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object FieldModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<FieldModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(Field::class) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			var name = annotation.getValue(Field::name)
			if (name.isNullOrBlank()) {
				name = varName
			}
			val isString = valueParameter.type.toTypeName() == String::class.asTypeName()
			FieldModel(name, varName, isString)
		}
	}
}