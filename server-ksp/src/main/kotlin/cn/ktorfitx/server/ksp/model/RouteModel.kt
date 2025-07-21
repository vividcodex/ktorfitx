package cn.ktorfitx.server.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation

internal sealed interface RouteModel {
	
	val path: String
	
	val annotation: KSAnnotation
}