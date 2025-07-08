package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.MockModel
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal object MockModelResolver {
	
	fun KSFunctionDeclaration.resolve(): MockModel? {
		val annotation = getKSAnnotationByType(ClassNames.Mock) ?: return null
		val className = annotation.getClassName("provider")!!
		val delay = annotation.getValueOrNull("delay") ?: 0L
		
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
		annotation.compileCheck(delay >= 0L) {
			val funName = simpleName.asString()
			"$funName 的注解的 delay 参数的值必须不小于 0L"
		}
		return MockModel(className, delay)
	}
}