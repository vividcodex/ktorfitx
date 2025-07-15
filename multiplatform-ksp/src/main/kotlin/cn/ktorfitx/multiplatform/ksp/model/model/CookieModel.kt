package cn.ktorfitx.multiplatform.ksp.model.model

internal class CookieModel(
	override val varName: String,
	val name: String,
	val maxAge: Int?,
	val expires: Long?,
	val domain: String?,
	val path: String?,
	val secure: Boolean?,
	val httpOnly: Boolean?,
	val extensions: Map<String, String>?
) : ValueParameterModel