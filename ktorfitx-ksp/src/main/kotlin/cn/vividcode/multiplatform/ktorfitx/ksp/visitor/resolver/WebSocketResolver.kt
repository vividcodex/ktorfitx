package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.WebSocketModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName

internal object WebSocketResolver {
	
	private val WebSocketClassName = ClassName.bestGuess(KtorfitxQualifiers.WEB_SOCKET)
	
	fun KSFunctionDeclaration.resolve(): WebSocketModel? {
		val annotation = getKSAnnotationByType(WebSocketClassName) ?: return null
		val url = annotation.getValue<String>("url") ?: return null
		return WebSocketModel(url)
	}
}