package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.annotation.Finally
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.FinallyModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/5 下午12:30
 *
 * 介绍：FinallyModelResolver
 */
@Suppress("unused")
internal data object FinallyModelResolver : ValueParameterModelResolver<FinallyModel> {
	
	private val finallyClassName by lazy { Finally::class.asClassName() }
	
	override fun KSFunctionDeclaration.resolve(): List<FinallyModel> {
		return this.parameters.filter {
			it.type.resolve().declaration.qualifiedName?.asString() == finallyClassName.canonicalName
		}.map {
			check(!it.type.resolve().isMarkedNullable) {
				"${qualifiedName!!.asString()} 方法中的 FinallyCallback? 使用了不被允许的可空类型"
			}
			val varName = it.name!!.asString()
			FinallyModel(varName)
		}
	}
}