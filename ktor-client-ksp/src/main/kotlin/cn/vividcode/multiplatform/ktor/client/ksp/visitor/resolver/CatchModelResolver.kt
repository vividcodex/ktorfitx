package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.annotation.Catch
import cn.vividcode.multiplatform.ktor.client.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.CatchModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Variance.INVARIANT
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/6 上午6:54
 *
 * 介绍：CatchModelResolver
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