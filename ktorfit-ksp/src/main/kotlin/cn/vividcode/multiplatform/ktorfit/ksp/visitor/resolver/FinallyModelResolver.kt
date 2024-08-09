package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.annotation.Finally
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.FinallyModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asClassName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/5 12:30
 *
 * 文件介绍：FinallyModelResolver
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