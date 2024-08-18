package cn.vividcode.multiplatform.ktorfitx.ksp.expends

private val LowerCamelCaseRegex by lazy {
	"^[a-z][a-zA-z0-9]*$".toRegex()
}

private val UpperCamelCaseRegex by lazy {
	"^[A-Z][a-zA-Z0-9]*$".toRegex()
}

/**
 * 判断是否是小驼峰命名
 */
internal fun String.isLowerCamelCase(): Boolean {
	return LowerCamelCaseRegex.matches(this)
}

/**
 * 判断是否是大驼峰命名
 */
internal fun String.isUpperCamelCase(): Boolean {
	return UpperCamelCaseRegex.matches(this)
}

/**
 * 改为小驼峰命名
 */
internal fun String.lowerCamelCase(): String {
	return when {
		'_' in this -> {
			this.split('_').joinToString {
				it.replaceFirstToUppercase()
			}.replaceFirstToLowercase()
		}
		
		this[0].isUpperCase() -> this.replaceFirstToLowercase()
		else -> this
	}
}

/**
 * 改为大驼峰命名
 */
internal fun String.upperCamelCase(): String {
	return when {
		'_' in this -> {
			this.split('_').joinToString {
				it.replaceFirstToUppercase()
			}
		}
		
		this[0].isLowerCase() -> this.replaceFirstToUppercase()
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