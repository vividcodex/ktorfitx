package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Path
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.PathModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/17 18:53
 *
 * 文件介绍：PathModelResolver
 */
internal object PathModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<PathModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getAnnotationByType(Path::class) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			val name = annotation.name.ifBlank { varName }
			PathModel(name, varName)
		}
	}
}