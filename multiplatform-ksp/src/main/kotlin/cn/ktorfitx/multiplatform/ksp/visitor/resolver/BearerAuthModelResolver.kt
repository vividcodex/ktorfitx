package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.multiplatform.annotation.BearerAuth
import cn.ktorfitx.multiplatform.ksp.model.model.BearerAuthModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.impl.hasAnnotation

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 16:16
 *
 * 文件介绍：BearerAuthModelResolver
 */
internal object BearerAuthModelResolver {
	
	fun KSFunctionDeclaration.resolve(): BearerAuthModel? {
		val hasBearerAuth = hasAnnotation(BearerAuth::class.qualifiedName!!)
		return if (hasBearerAuth) BearerAuthModel else null
	}
}