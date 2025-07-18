package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.TimeoutModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal fun KSFunctionDeclaration.resolveTimeoutModel(): TimeoutModel? {
	val annotation = this.getKSAnnotationByType(TypeNames.Timeout) ?: return null
	val requestTimeoutMillis = annotation.getValueOrNull<Long>("requestTimeoutMillis")?.takeIf { it >= 0L }
	val connectTimeoutMillis = annotation.getValueOrNull<Long>("connectTimeoutMillis")?.takeIf { it >= 0L }
	val socketTimeoutMillis = annotation.getValueOrNull<Long>("socketTimeoutMillis")?.takeIf { it >= 0L }
	if (requestTimeoutMillis == null && connectTimeoutMillis == null && socketTimeoutMillis == null) return null
	return TimeoutModel(requestTimeoutMillis, connectTimeoutMillis, socketTimeoutMillis)
}