package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.multiplatform.annotation.Query
import cn.ktorfitx.multiplatform.ksp.expends.getKSAnnotationByType
import cn.ktorfitx.multiplatform.ksp.expends.getValue
import cn.ktorfitx.multiplatform.ksp.model.model.QueryModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/17 18:53
 *
 * 文件介绍：QueryModelResolver
 */
internal object QueryModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<QueryModel> {
		return this.parameters.mapNotNull { valueParameter ->
			val annotation = valueParameter.getKSAnnotationByType(Query::class) ?: return@mapNotNull null
			var name = annotation.getValue(Query::name)
			val varName = valueParameter.name!!.asString()
			if (name.isNullOrBlank()) {
				name = varName
			}
			QueryModel(name, varName)
		}
	}
}