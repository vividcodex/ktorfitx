package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.ksp.expends.getArgumentArrayValue
import cn.vividcode.multiplatform.ktorfit.ksp.expends.getArgumentClassName
import cn.vividcode.multiplatform.ktorfit.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.MockModel
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
	
	private val mockClassName = ClassName("cn.vividcode.multiplatform.ktorfit.annotation", "Mock")
	private val statusSuccessClassName by lazy {
		ClassName("cn.vividcode.multiplatform.ktorfit.api.mock", "MockStatus", "SUCCESS")
	}
	
	fun KSFunctionDeclaration.resolve(resolver: Resolver): MockModel? {
		val annotation = getKSAnnotationByType(mockClassName) ?: return null
		val providerClassName = annotation.getArgumentClassName("provider")!!
		resolver.getClassDeclarationByName(providerClassName.canonicalName)!!.also {
			check(it.classKind == ClassKind.OBJECT) {
				"${it.simpleName.asString()} 的 ClassKind 只允许是 ${ClassKind.OBJECT} 类型"
			}
		}
		val status = annotation.getArgumentClassName("status") ?: statusSuccessClassName
		val delayRange = annotation.getArgumentArrayValue("delayRange") ?: arrayOf(200L)
		check(delayRange.size == 1 || (delayRange.size == 2 && delayRange[0] <= delayRange[1])) {
			"delayRange 必须有 1-2 个时间，并且 delayRange[0] <= delayRange[1]"
		}
		return MockModel(providerClassName, status, delayRange)
	}
}