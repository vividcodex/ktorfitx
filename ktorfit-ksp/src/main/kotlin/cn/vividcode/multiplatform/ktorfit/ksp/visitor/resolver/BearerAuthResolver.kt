package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.annotation.BearerAuth
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.BearerAuthModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.impl.hasAnnotation

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 16:16
 *
 * 文件介绍：BearerAuthResolver
 */
internal object BearerAuthResolver {
	
	fun KSFunctionDeclaration.resolve(): BearerAuthModel? {
		val hasBearerAuth = hasAnnotation(BearerAuth::class.qualifiedName!!)
		return if (hasBearerAuth) BearerAuthModel else null
	}
}