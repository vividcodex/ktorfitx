package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.WebSocketModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal fun KSFunctionDeclaration.resolveWebSocketModel(): WebSocketModel? {
	val hasWebSocket = hasAnnotation(TypeNames.WebSocket)
	return if (hasWebSocket) WebSocketModel else null
}