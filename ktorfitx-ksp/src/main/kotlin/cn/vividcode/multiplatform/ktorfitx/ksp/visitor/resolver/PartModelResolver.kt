package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Part
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.PartModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/17 18:53
 *
 * 文件介绍：PartModelResolver
 */
internal object PartModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<PartModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(Part::class) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			var name = annotation.getValue(Part::name)
			if (name.isNullOrBlank()) {
				name = varName
			}
			PartModel(name, varName)
		}
	}
}