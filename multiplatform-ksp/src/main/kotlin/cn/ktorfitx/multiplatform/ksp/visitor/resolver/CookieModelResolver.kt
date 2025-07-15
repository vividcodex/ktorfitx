package cn.ktorfitx.multiplatform.ksp.visitor.resolver

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValueOrNull
import cn.ktorfitx.common.ksp.util.expends.getValuesOrNull
import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import cn.ktorfitx.multiplatform.ksp.model.model.CookieModel
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

internal object CookieModelResolver {
	
	fun KSFunctionDeclaration.resolves(): List<CookieModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Cookie) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			val typeName = parameter.type.toTypeName()
			compileCheck(!typeName.isNullable) {
				"${simpleName.asString()} 函数的 $varName 参数不允许为可空类型"
			}
			compileCheck(typeName == ClassNames.String) {
				"${simpleName.asString()} 函数的 $varName 参数只允许为 String 类型"
			}
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			val maxAge = annotation.getValueOrNull<Int>("maxAge")?.takeIf { it >= 0 }
			val expires = annotation.getValueOrNull<Long>("expires")?.takeIf { it >= 0L }
			val domain = annotation.getValueOrNull<String>("domain")?.takeIf { it.isNotBlank() }
			val path = annotation.getValueOrNull<String>("path")?.takeIf { it.isNotBlank() }
			val secure = annotation.getValueOrNull<Boolean>("secure")
			val httpOnly = annotation.getValueOrNull<Boolean>("httpOnly")
			val extensions = annotation.getValuesOrNull<String>("extensions")
				?.takeIf { it.isNotEmpty() }
				?.let {
					it.associate { entry ->
						val parts = entry.split(":")
						parameter.compileCheck(parts.size == 2) {
							"${simpleName.asString()} 函数的 $varName 参数的 @Cookie 注解上 extensions 参数格式错误，应该为 <key>:<value> 形式"
						}
						val key = parts[0].trim()
						val value = parts[1].trim()
						parameter.compileCheck(key.isNotBlank()) {
							"${simpleName.asString()} 函数的 $varName 参数的 @Cookie 注解上 extensions 参数格式错误，key 不能为空"
						}
						key to value
					}
				}
			CookieModel(varName, name, maxAge, expires, domain, path, secure, httpOnly, extensions)
		}
	}
}