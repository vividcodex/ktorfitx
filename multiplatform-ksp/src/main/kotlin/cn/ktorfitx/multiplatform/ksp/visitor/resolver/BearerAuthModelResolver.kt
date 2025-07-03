package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.BearerAuthModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object BearerAuthModelResolver {
	
	fun KSFunctionDeclaration.resolve(): BearerAuthModel? {
		val hasBearerAuth = hasAnnotation(ClassNames.BearerAuth)
		return if (hasBearerAuth) BearerAuthModel else null
	}
}