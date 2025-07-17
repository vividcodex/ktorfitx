package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.multiplatform.ksp.model.model.*
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal object ModelResolvers {
	
	/**
	 * 获取所有 FunModel
	 */
	fun KSFunctionDeclaration.getAllFunModels(): List<FunModel> {
		val models = mutableListOf<FunModel?>()
		val webSocketModel = this.resolveWebSocketModel()
		models += webSocketModel
		models += this.resolveApiModel(webSocketModel != null)
		models += this.resolveHeadersModel()
		models += this.resolveMockModel()
		models += this.resolveBearerAuthModel()
		return models.filterNotNull()
	}
	
	/**
	 * 获取所有 ParameterModel
	 */
	fun KSFunctionDeclaration.getAllParameterModel(isWebSocket: Boolean): List<ParameterModel> {
		return this.resolveParameterModels(isWebSocket)
	}
	
	/**
	 * 获取所有 ValueParameterModel
	 */
	fun KSFunctionDeclaration.getAllValueParameterModels(): List<ValueParameterModel> {
		val models = mutableListOf<ValueParameterModel?>()
		models += this.resolveBodyModel()
		models += this.resolveQueryModels()
		models += this.resolvePartModels()
		models += this.resolveFieldModels()
		models += this.resolvePathModels()
		models += this.resolveHeaderModels()
		models += this.resolveCookieModels()
		models += this.resolveAttributeModels()
		
		val filterModels = models.filterNotNull()
		val targetTypes = setOf(BodyModel::class, PartModel::class, FieldModel::class)
		val incompatibleTypeCount = targetTypes.count { kClass ->
			filterModels.any { kClass.isInstance(it) }
		}
		
		this.compileCheck(incompatibleTypeCount <= 1) {
			"${simpleName.asString()} 函数不能同时使用 @Body, @Part 和 @Field 注解 $incompatibleTypeCount"
		}
		return models.filterNotNull()
	}
}