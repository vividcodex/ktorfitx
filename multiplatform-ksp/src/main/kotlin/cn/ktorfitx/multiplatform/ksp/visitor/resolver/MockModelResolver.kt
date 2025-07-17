package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.multiplatform.ksp.constants.TypeNames
import cn.ktorfitx.multiplatform.ksp.model.model.MockModel
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSFunctionDeclaration.resolveMockModel(): MockModel? {
	val annotation = getKSAnnotationByType(TypeNames.Mock) ?: return null
	val className = annotation.getClassName("provider")
	val delay = annotation.getValueOrNull("delay") ?: 0L
	
	annotation.compileCheck(className != TypeNames.MockProvider) {
		"${simpleName.asString()} 函数上的 @Mock 注解的 provider 参数不允许使用 MockProvider::class"
	}
	val classDeclaration = annotation.getArgumentKSClassDeclaration("provider")!!
	val classKind = classDeclaration.classKind
	classDeclaration.compileCheck(classKind == ClassKind.OBJECT) {
		"${className.simpleName} 类不允许使用 ${classKind.code} 类型，请使用 object 类型"
	}
	classDeclaration.compileCheck(!classDeclaration.modifiers.contains(Modifier.PRIVATE)) {
		"${className.simpleName} 类不允许使用 private 访问权限"
	}
	
	val mockReturnType = classDeclaration.superTypes
		.map { it.resolve() }
		.find { it.toTypeName().rawType == TypeNames.MockProvider }
		?.arguments
		?.firstOrNull()
		?.type
		?.toTypeName()
	classDeclaration.compileCheck(mockReturnType != null) {
		"${className.simpleName} 类必须实现 MockProvider<T> 接口"
	}
	val returnType = this.returnType!!.toTypeName().let {
		if (it.rawType == TypeNames.Result) {
			it as ParameterizedTypeName
			it.typeArguments.first()
		} else {
			it
		}
	}
	this.compileCheck(returnType == mockReturnType) {
		"${simpleName.asString()} 函数的 provider 类型与返回值不一致，应该为 $returnType, 实际为 $mockReturnType"
	}
	annotation.compileCheck(delay >= 0L) {
		val funName = simpleName.asString()
		"$funName 的注解的 delay 参数的值必须不小于 0L"
	}
	return MockModel(className, delay)
}