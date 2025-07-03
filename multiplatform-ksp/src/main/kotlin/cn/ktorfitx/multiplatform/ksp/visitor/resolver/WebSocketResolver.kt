package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.expends.hasAnnotation
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.WebSocketModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object WebSocketResolver {
	
	fun KSFunctionDeclaration.resolve(): WebSocketModel? {
		val hasWebSocket = hasAnnotation(ClassNames.WebSocket)
		return if (hasWebSocket) WebSocketModel else null
	}
}