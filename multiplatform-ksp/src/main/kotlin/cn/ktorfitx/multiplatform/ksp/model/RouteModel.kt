package cn.ktorfitx.multiplatform.ksp.model

internal sealed interface RouteModel {
	
	val url: Url
}

internal sealed interface Url

internal class StaticUrl(
	val url: String
) : Url

internal class DynamicUrl(
	val varName: String
) : Url