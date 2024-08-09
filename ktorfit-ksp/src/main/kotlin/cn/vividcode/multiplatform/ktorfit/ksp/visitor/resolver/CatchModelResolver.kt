package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.annotation.Catch
import cn.vividcode.multiplatform.ktorfit.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.CatchModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Variance.INVARIANT
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/6 6:54
 *
 * 文件介绍：CatchModelResolver
 */
@Suppress("unused")
internal data object CatchModelResolver : ValueParameterModelResolver<CatchModel> {
	
	private val catchQualifiedName by lazy { Catch::class.qualifiedName }
	
	override fun KSFunctionDeclaration.resolve(): List<CatchModel> {
		return this.parameters.filter {
			it.type.resolve().declaration.qualifiedName?.asString() == catchQualifiedName
		}.map {
			val type = it.type.resolve()
			val typeArgument = type.arguments.first()
			val parameterizedTypeName = type.toTypeName() as ParameterizedTypeName
			check(typeArgument.variance == INVARIANT) {
				"${qualifiedName!!.asString()} 方法中的 ${parameterizedTypeName.simpleName} 使用了不被允许的 ${typeArgument.variance.label} 类型"
			}
			check(!type.isMarkedNullable) {
				"${qualifiedName!!.asString()} 方法中的 ${parameterizedTypeName.simpleName}? 使用了不被允许的可空类型"
			}
			val varName = it.name!!.asString()
			val exceptionTypeName = parameterizedTypeName.typeArguments.first()
			CatchModel(varName, exceptionTypeName)
		}
	}
}