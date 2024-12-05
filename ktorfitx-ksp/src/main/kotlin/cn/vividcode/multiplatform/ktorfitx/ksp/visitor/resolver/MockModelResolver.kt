package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithMockProviderDelayRange
import cn.vividcode.multiplatform.ktorfitx.ksp.check.checkWithMockProviderType
import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassName
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValues
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.MockModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/2 22:50
 *
 * 文件介绍：MockModelResolver
 */
internal object MockModelResolver {
	
	private val mockClassName by lazy {
		ClassName.bestGuess(KtorfitxQualifiers.MOCK)
	}
	private val statusSuccessClassName by lazy {
		ClassName(KtorfitxQualifiers.PACKAGE_API_MOCK, "MockStatus", "SUCCESS")
	}
	
	fun KSFunctionDeclaration.resolve(): MockModel? {
		val annotation = getKSAnnotationByType(mockClassName) ?: return null
		val mockProviderClassName = annotation.getClassName("provider")!!
		val delayRange = annotation.getValues("delayRange") ?: arrayOf(200L)
		with(annotation) {
			val funName = simpleName.asString()
			this.checkWithMockProviderType(mockProviderClassName, funName)
			this.checkWithMockProviderDelayRange(delayRange, funName)
		}
		val status = annotation.getClassName("status") ?: statusSuccessClassName
		return MockModel(mockProviderClassName, status, delayRange)
	}
}