package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithRequestMethodCount
import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithUrlRegex
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.isHttpOrHttps
import cn.vividcode.multiplatform.ktorfitx.ksp.model.RequestMethod
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.ApiModel
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 14:17
 *
 * 文件介绍：ApiModelResolver
 */
internal object ApiModelResolver {
	
	fun KSFunctionDeclaration.resolve(): ApiModel {
		val annotations = RequestMethod.entries.mapNotNull {
			getKSAnnotationByType(it.annotation)
		}
		this.checkWithRequestMethodCount(annotations)
		val annotation = annotations.first()
		val requestFunName = annotation.shortName.asString().lowercase()
		return ApiModel(requestFunName, getUrl(annotation))
	}
	
	private fun KSFunctionDeclaration.getUrl(annotation: KSAnnotation): String {
		val url = annotation.getValue<String>("url")!!
		if (url.isHttpOrHttps()) return url
		this.checkWithUrlRegex(url, annotation)
		return if (url.startsWith('/')) url else "/$url"
	}
}