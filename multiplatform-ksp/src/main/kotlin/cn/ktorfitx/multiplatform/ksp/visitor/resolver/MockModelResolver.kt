package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.constants.PackageNames
import cn.ktorfitx.multiplatform.ksp.model.model.MockModel
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName

internal object MockModelResolver {
	
	private val statusSuccessClassName by lazy {
		ClassName(PackageNames.KTORFITX_MOCK, "MockStatus", "SUCCESS")
	}
	
	fun KSFunctionDeclaration.resolve(): MockModel? {
		val annotation = getKSAnnotationByType(ClassNames.Mock) ?: return null
		val className = annotation.getClassName("provider")!!
		val delayRange = annotation.getValues("delayRange") ?: arrayOf(200L)
		
		annotation.compileCheck(className != ClassNames.MockProvider) {
			val funName = this.simpleName.asString()
			"$funName 方法上的 @Mock 注解的 provider 参数不允许使用 MockProvider::class"
		}
		val classDeclaration = annotation.getArgumentKSClassDeclaration("provider")!!
		val classKind = classDeclaration.classKind
		classDeclaration.compileCheck(classKind == ClassKind.OBJECT) {
			"${className.simpleName} 类不允许使用 ${classKind.code} 类型，请使用 object 类型"
		}
		classDeclaration.compileCheck(!classDeclaration.modifiers.contains(Modifier.PRIVATE)) {
			"${className.simpleName} 类不允许使用 private 访问权限"
		}
		annotation.compileCheck(delayRange.size == 1 || (delayRange.size == 2 && delayRange[0] <= delayRange[1])) {
			val funName = simpleName.asString()
			"$funName 方法上的 @Mock 注解的 delayRange 参数只允许1个固定延迟时长参数或2个范围随机延长参数，并且2个参数的情况下必须 delayRange[0] <= delayRange[1]"
		}
		val status = annotation.getClassName("status") ?: statusSuccessClassName
		return MockModel(className, status, delayRange)
	}
}