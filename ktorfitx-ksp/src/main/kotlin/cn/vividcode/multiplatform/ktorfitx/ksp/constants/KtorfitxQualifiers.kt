package cn.vividcode.multiplatform.ktorfitx.ksp.constants

/**
 * Ktorfitx 相关全类名
 */
internal object KtorfitxQualifiers {
	
	private const val PACKAGE_ANNOTATION = "cn.vividcode.multiplatform.ktorfitx.annotation"
	
	const val PACKAGE_CORE = "cn.vividcode.multiplatform.ktorfitx.core"
	
	const val PACKAGE_CORE_EXPENDS = "cn.vividcode.multiplatform.ktorfitx.core.expends"
	
	const val PACKAGE_MOCK = "cn.vividcode.multiplatform.ktorfitx.mock"
	
	const val PACKAGE_MOCK_CONFIG = "$PACKAGE_MOCK.config"
	
	private const val PACKAGE_WEB_SOCKETS = "cn.vividcode.multiplatform.ktorfitx.websockets"
	
	const val API = "$PACKAGE_ANNOTATION.Api"
	
	const val BODY = "$PACKAGE_ANNOTATION.Body"
	
	const val PART = "$PACKAGE_ANNOTATION.Part"
	
	const val FIELD = "$PACKAGE_ANNOTATION.Field"
	
	const val HEADER = "$PACKAGE_ANNOTATION.Header"
	
	const val PATH = "$PACKAGE_ANNOTATION.Path"
	
	const val QUERY = "$PACKAGE_ANNOTATION.Query"
	
	const val WEB_SOCKET_SESSION_HANDLER = "$PACKAGE_WEB_SOCKETS.WebSocketSessionHandler"
	
	const val EXCEPTION_LISTENERS = "$PACKAGE_ANNOTATION.ExceptionListeners"
	
	const val EXCEPTION_LISTENER = "$PACKAGE_CORE.exception.ExceptionListener"
	
	const val API_RESULT = "$PACKAGE_CORE.model.ApiResult"
	
	const val API_SCOPE = "$PACKAGE_CORE.scope.ApiScope"
	
	const val DEFAULT_API_SCOPE = "$PACKAGE_CORE.scope.DefaultApiScope"
	
	const val KTORFIT = "$PACKAGE_CORE.Ktorfit"
	
	const val KTORFIT_CONFIG = "$PACKAGE_CORE.config.KtorfitConfig"
	
	const val MOCK = "$PACKAGE_ANNOTATION.Mock"
	
	const val MOCK_CLIENT = "$PACKAGE_MOCK.MockClient"
	
	const val MOCK_PROVIDER = "$PACKAGE_MOCK.MockProvider"
}