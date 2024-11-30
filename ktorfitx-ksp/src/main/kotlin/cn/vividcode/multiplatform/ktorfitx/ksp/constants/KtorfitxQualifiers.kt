package cn.vividcode.multiplatform.ktorfitx.ksp.constants

/**
 * Ktorfitx 相关全类名
 */
internal object KtorfitxQualifiers {
	
	const val PACKAGE_ANNOTATION = "cn.vividcode.multiplatform.ktorfitx.annotation"
	
	const val PACKAGE_API = "cn.vividcode.multiplatform.ktorfitx.api"
	
	const val PACKAGE_API_EXPENDS = "cn.vividcode.multiplatform.ktorfitx.api.expends"
	
	const val PACKAGE_API_MOCK = "cn.vividcode.multiplatform.ktorfitx.api.mock"
	
	const val API = "$PACKAGE_ANNOTATION.Api"
	
	const val BODY = "$PACKAGE_ANNOTATION.Body"
	
	const val FORM = "$PACKAGE_ANNOTATION.Form"
	
	const val HEADER = "$PACKAGE_ANNOTATION.Header"
	
	const val PATH = "$PACKAGE_ANNOTATION.Path"
	
	const val QUERY = "$PACKAGE_ANNOTATION.Query"
	
	const val EXCEPTION_LISTENERS = "$PACKAGE_ANNOTATION.ExceptionListeners"
	
	const val EXCEPTION_LISTENER = "$PACKAGE_API.exception.ExceptionListener"
	
	const val RESULT_BODY = "$PACKAGE_API.model.ResultBody"
	
	const val API_SCOPE = "$PACKAGE_API.scope.ApiScope"
	
	const val DEFAULT_API_SCOPE = "$PACKAGE_API.scope.DefaultApiScope"
	
	const val KTORFIT = "$PACKAGE_API.Ktorfit"
	
	const val KTORFIT_CONFIG = "$PACKAGE_API.config.KtorfitConfig"
	
	const val MOCK = "$PACKAGE_ANNOTATION.Mock"
	
	const val MOCK_CLIENT = "$PACKAGE_API_MOCK.MockClient"
	
	const val MOCK_PROVIDER = "$PACKAGE_API_MOCK.MockProvider"
}