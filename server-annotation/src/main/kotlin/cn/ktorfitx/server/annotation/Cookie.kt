package cn.ktorfitx.server.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
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