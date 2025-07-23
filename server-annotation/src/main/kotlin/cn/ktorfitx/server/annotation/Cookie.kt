package cn.ktorfitx.server.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Cookie(
	val name: String = "",
	val encoding: CookieEncoding = CookieEncoding.URI_ENCODING
)

enum class CookieEncoding {
	
	RAW,
	
	DQUOTES,
	
	URI_ENCODING,
	
	BASE64_ENCODING
}