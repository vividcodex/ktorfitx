package cn.ktorfitx.common.ksp.util.expends

private val lowerCamelCaseRegex by lazy {
	"^[a-z][a-zA-Z0-9]*$".toRegex()
}

/**
 * 判断是否是小驼峰命名
 */
fun String.isLowerCamelCase(): Boolean {
	return lowerCamelCaseRegex.matches(this)
}

/**
 * 改为小驼峰命名
 */
fun String.toLowerCamelCase(): String {
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
fun String.replaceFirstToLowercase(): String {
	return this.replaceFirstChar { it.lowercaseChar() }
}

/**
 * 替换首字母为大写
 */
fun String.replaceFirstToUppercase(): String {
	return this.replaceFirstChar { it.uppercaseChar() }
}

private const val HTTP = "http://"
private const val HTTPS = "https://"
private const val WS = "ws://"
private const val WSS = "wss://"

/**
 * http or https
 */
fun String.isHttpOrHttps(): Boolean {
	return this.startsWith(HTTP) || this.startsWith(HTTPS)
}

/**
 * ws or wss
 */
fun String.isWSOrWSS(): Boolean {
	return this.startsWith(WS) || this.startsWith(WSS)
}

private val headerCaseRegex = "([a-z])([A-Z])".toRegex()

fun String.camelToHeaderCase(): String {
	return this.replace(headerCaseRegex) {
		"${it.groupValues[1]}-${it.groupValues[2]}"
	}.replaceFirstChar { it.uppercaseChar() }
}

fun String.isValidRegex(): Boolean {
	return try {
		Regex(this)
		true
	} catch (_: Exception) {
		false
	}
}