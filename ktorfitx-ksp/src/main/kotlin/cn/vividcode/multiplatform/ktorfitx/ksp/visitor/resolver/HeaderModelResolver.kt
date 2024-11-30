package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Header
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.HeaderModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/9 22:12
 *
 * 文件介绍：HeaderModelResolver
 */
internal object HeaderModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<HeaderModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val header = valueParameter.getAnnotationByType(Header::class) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			val name = header.name.ifBlank {
				varName.replace("([a-z])([A-Z])".toRegex()) {
					"${it.groupValues[1]}-${it.groupValues[2]}"
				}.replaceFirstChar { it.uppercase() }
			}
			HeaderModel(name, varName)
		}
	}
}