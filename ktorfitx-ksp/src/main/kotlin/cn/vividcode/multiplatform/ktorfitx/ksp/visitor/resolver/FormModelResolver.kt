package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Form
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.FormModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/17 18:53
 *
 * 文件介绍：FormModelResolver
 */
internal object FormModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<FormModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getAnnotationByType(Form::class) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			val name = annotation.name.ifBlank { varName }
			FormModel(name, varName)
		}
	}
}