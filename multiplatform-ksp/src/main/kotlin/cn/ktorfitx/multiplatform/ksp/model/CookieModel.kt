package cn.ktorfitx.multiplatform.ksp.model

internal class CookieModel(
	val varName: String,
	val name: String,
	val maxAge: Int?,
	val expires: Long?,
	val domain: String?,
	val path: String?,
	val secure: Boolean?,
	val httpOnly: Boolean?,
	val extensions: Map<String, String?>?
)