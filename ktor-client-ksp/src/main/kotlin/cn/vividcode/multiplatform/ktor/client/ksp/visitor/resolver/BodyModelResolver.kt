package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.annotation.Body
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.BodyModel
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午1:25
 *
 * 介绍：BodyModelResolver
 */
@Suppress("unused")
internal data object BodyModelResolver : ValueParameterModelResolver<BodyModel> {
	
	@OptIn(KspExperimental::class)
	override fun KSFunctionDeclaration.resolve(): List<BodyModel> {
		return this.parameters.filter { it.isAnnotationPresent(Body::class) }
			.also { check(it.size <= 1) { "@Body 不允许在同一个方法参数列表上多次使用" } }
			.map {
				val varName = it.name!!.asString()
				BodyModel(varName)
			}
	}
}