package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.annotation.Body
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.BodyModel
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 13:25
 *
 * 文件介绍：BodyModelResolver
 */
internal object BodyModelResolver {
	
	@OptIn(KspExperimental::class)
	fun KSFunctionDeclaration.resolve(): BodyModel? {
		return this.parameters.filter { it.isAnnotationPresent(Body::class) }
			.also {
				check(it.size <= 1) { "@Body 不允许在同一个方法参数列表上多次使用" }
			}.firstOrNull()?.let {
				BodyModel(it.name!!.asString())
			}
	}
}