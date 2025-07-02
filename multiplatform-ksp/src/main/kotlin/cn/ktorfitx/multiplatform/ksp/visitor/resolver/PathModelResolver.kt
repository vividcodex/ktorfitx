package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.multiplatform.annotation.Path
import cn.ktorfitx.multiplatform.ksp.expends.getKSAnnotationByType
import cn.ktorfitx.multiplatform.ksp.expends.getValue
import cn.ktorfitx.multiplatform.ksp.model.model.PathModel
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
			val annotation = valueParameter.getKSAnnotationByType(Path::class) ?: return@mapNotNull null
			val varName = valueParameter.name!!.asString()
			val name = annotation.getValue(Path::name) ?: varName
			PathModel(name, varName, valueParameter)
		}
	}
}