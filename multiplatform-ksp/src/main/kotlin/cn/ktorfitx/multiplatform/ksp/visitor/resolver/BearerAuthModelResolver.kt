package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.BearerAuthModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal fun KSFunctionDeclaration.resolveBearerAuthModel(): BearerAuthModel? {
	val hasBearerAuth = hasAnnotation(TypeNames.BearerAuth)
	return if (hasBearerAuth) BearerAuthModel else null
}