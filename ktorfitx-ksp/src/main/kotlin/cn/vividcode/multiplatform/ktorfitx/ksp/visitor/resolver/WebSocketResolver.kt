package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.WebSocketModel
import cn.vividcode.multiplatform.ktorfitx.websockets.WebSocket
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.impl.hasAnnotation

internal object WebSocketResolver {
	
	fun KSFunctionDeclaration.resolve(): WebSocketModel? {
		val hasWebSocket = hasAnnotation(WebSocket::class.qualifiedName!!)
		return if (hasWebSocket) WebSocketModel else null
	}
}