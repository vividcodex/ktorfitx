package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.annotation.Mock
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.MockModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/2 22:50
 *
 * 文件介绍：MockModelResolver
 */
@Suppress("unused")
internal data object MockModelResolver : FunctionModelResolver<MockModel> {
	
	override fun KSFunctionDeclaration.resolve(): MockModel? {
		val mock = getAnnotationByType(Mock::class) ?: return null
		if (mock.name.isBlank()) {
			error("${qualifiedName!!.asString()} 的 @Mock 的名称不能为空")
		}
		return MockModel(mock.name)
	}
}