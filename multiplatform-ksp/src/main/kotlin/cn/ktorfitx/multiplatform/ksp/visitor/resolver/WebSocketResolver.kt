package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.multiplatform.annotation.WebSocket
import cn.ktorfitx.multiplatform.ksp.model.model.WebSocketModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.impl.hasAnnotation

internal object WebSocketResolver {
	
	fun KSFunctionDeclaration.resolve(): WebSocketModel? {
		val hasWebSocket = hasAnnotation(WebSocket::class.qualifiedName!!)
		return if (hasWebSocket) WebSocketModel else null
	}
}