package cn.vividcode.multiplatform.ktorfitx.ksp.constants

/**
 * Ktor 相关全类名
 */
internal object KtorQualifiers {
	
	const val KTOR = "io.ktor"
	
	const val PACKAGE_HTTP = "io.ktor.http"
	
	private const val PACKAGE_CLIENT = "io.ktor.client"
	
	const val PACKAGE_REQUEST = "io.ktor.client.request"
	
	const val PACKAGE_REQUEST_FORMS = "io.ktor.client.request.forms"
	
	const val HTTP_CLIENT = "$PACKAGE_CLIENT.HttpClient"
}