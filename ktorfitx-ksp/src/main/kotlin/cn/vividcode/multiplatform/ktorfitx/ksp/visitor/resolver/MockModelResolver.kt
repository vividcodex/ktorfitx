package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassName
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValues
import cn.vividcode.multiplatform.ktorfitx.ksp.model.model.MockModel
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/2 22:50
 *
 * 文件介绍：MockModelResolver
 */
internal object MockModelResolver {
	
	private val mockClassName = ClassName("cn.vividcode.multiplatform.ktorfitx.annotation", "Mock")
	private val statusSuccessClassName by lazy {
		ClassName("cn.vividcode.multiplatform.ktorfitx.api.mock", "MockStatus", "SUCCESS")
	}
	private val mockProviderClassName = ClassName("cn.vividcode.multiplatform.ktorfitx.api.mock", "MockProvider")
	
	fun KSFunctionDeclaration.resolve(resolver: Resolver): MockModel? {
		val annotation = getKSAnnotationByType(mockClassName) ?: return null
		val mockProviderClassName = annotation.getClassName("provider")!!
		check(mockProviderClassName != this@MockModelResolver.mockProviderClassName) {
			"${this.simpleName.asString()} 的 @Mock 注解的参数 provider 不能使用 MockProvider::class，请继承 MockProvider 并且类型为 object 的实现"
		}
		resolver.getClassDeclarationByName(mockProviderClassName.canonicalName)!!.also {
			check(it.classKind == ClassKind.OBJECT) {
				"${it.simpleName.asString()} 的 ClassKind 只允许是 ${ClassKind.OBJECT} 类型的"
			}
		}
		val status = annotation.getClassName("status") ?: statusSuccessClassName
		val delayRange = annotation.getValues("delayRange") ?: arrayOf(200L)
		check(delayRange.size == 1 || (delayRange.size == 2 && delayRange[0] <= delayRange[1])) {
			"${this.simpleName.asString()} 的 @Mock 注解的参数 delayRange 必须有1个固定延迟或2个区间随机延迟，并且前者必须不大于后者"
		}
		return MockModel(mockProviderClassName, status, delayRange)
	}
}