package cn.vividcode.multiplatform.ktorfitx.ksp.expends

private val LowerCamelCaseRegex by lazy {
	"^[a-z][a-zA-Z0-9]*$".toRegex()
}

/**
 * 判断是否是小驼峰命名
 */
internal fun String.isLowerCamelCase(): Boolean {
	return LowerCamelCaseRegex.matches(this)
}

/**
 * 改为小驼峰命名
 */
internal fun String.lowerCamelCase(): String {
	return when {
		'_' in this -> {
			this.split('_').joinToString("") {
				it.replaceFirstToUppercase()
			}.replaceFirstToLowercase()
		}
		
		this[0].isUpperCase() -> this.replaceFirstToLowercase()
		else -> this
	}
}

/**
 * 替换首字母为小写
 */
internal fun String.replaceFirstToLowercase(): String {
	return this.replaceFirstChar { it.lowercaseChar() }
}

/**
 * 替换首字母为大写
 */
internal fun String.replaceFirstToUppercase(): String {
	return this.replaceFirstChar { it.uppercaseChar() }
}

private const val HTTP = "http://"
private const val HTTPS = "https://"
private const val WS = "ws://"
private const val WSS = "wss://"

/**
 * 是否是 http:// 或者 https://
 */
internal fun String.isHttpOrHttps(): Boolean {
	return this.startsWith(HTTP) || this.startsWith(HTTPS)
}

/**
 * 是否是 ws:// 或者 wss:// 开头
 */
internal fun String.isWSOrWSS(): Boolean {
	return this.startsWith(WS) || this.startsWith(WSS)
}