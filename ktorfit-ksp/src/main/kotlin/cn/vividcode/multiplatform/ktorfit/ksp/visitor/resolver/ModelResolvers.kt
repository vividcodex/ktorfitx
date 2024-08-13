package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.ksp.model.model.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 9:42
 *
 * 文件介绍：ParameterModelResolver
 */
internal object ModelResolvers {
	
	/**
	 * 获取所有 ParameterModel
	 */
	fun KSFunctionDeclaration.getAllParameterModel(): List<ParameterModel> {
		return this.parameters.map {
			val varName = it.name!!.asString()
			val typeName = it.type.resolve().toTypeName()
			ParameterModel(varName, typeName)
		}
	}
	
	/**
	 * 获取所有 ValueParameterModel
	 */
	fun KSFunctionDeclaration.getAllValueParameterModels(): List<ValueParameterModel> {
		return buildList<ValueParameterModel?> {
			this += with(BodyModelResolver) { resolve() }
			this += with(CatchModelResolver) { resolve() }
			this += with(EncryptValueParameterModelsResolver) { resolve() }
			this += with(FinallyModelResolver) { resolve() }
			this += with(HeaderModelsResolver) { resolve() }
		}.filterNotNull().also { models ->
			check(!(models.any { it is BodyModel } && models.any { it is FormModel })) {
				"${qualifiedName!!.asString()} 方法不能同时使用 @Body 和 @Form 注解"
			}
		}
	}
	
	/**
	 * 获取所有 FunctionModel
	 */
	fun KSFunctionDeclaration.getAllFunctionModels(resolver: Resolver): List<FunctionModel> = buildList {
		this += with(ApiModelResolver) { resolve() }
		this += with(HeadersModelResolver) { resolve() }
		this += with(MockModelResolver) { resolve(resolver) }
		this += with(BearerAuthResolver) { resolve() }
	}.filterNotNull()
}