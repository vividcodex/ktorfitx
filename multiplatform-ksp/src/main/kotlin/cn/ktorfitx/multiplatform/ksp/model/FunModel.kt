package cn.ktorfitx.multiplatform.ksp.model

internal class FunModel(
	val funName: String,
	val returnModel: ReturnModel,
	val parameterModels: List<ParameterModel>,
	val routeModel: RouteModel,
	val mockModel: MockModel?,
	val hasBearerAuth: Boolean,
	val isPrepareType: Boolean,
	val timeoutModel: TimeoutModel?,
	val queryModels: List<QueryModel>,
	val pathModels: List<PathModel>,
	val cookieModels: List<CookieModel>,
	val attributeModels: List<AttributeModel>,
	val headerModels: List<HeaderModel>,
	val headersModel: HeadersModel?,
	val requestBodyModel: RequestBodyModel?,
)