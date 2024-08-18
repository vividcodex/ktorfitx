package cn.vividcode.multiplatform.ktorfitx.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getClassName
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getKSAnnotationByType
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.getValue
import cn.vividcode.multiplatform.ktorfitx.ksp.expends.simpleName
import cn.vividcode.multiplatform.ktorfitx.ksp.model.EncryptInfo
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/17 19:44
 *
 * 文件介绍：EncryptUtil
 */
internal object EncryptInfoResolver {
	
	private val encryptClassName = ClassName("cn.vividcode.multiplatform.ktorfitx.annotation", "Encrypt")
	
	fun KSValueParameter.resolve(funName: String, varName: String): EncryptInfo? {
		val annotation = this.getKSAnnotationByType(encryptClassName) ?: return null
		val typeName = this.type.toTypeName()
		check(!typeName.isNullable && typeName == String::class.asClassName()) {
			"$funName 方法的 $varName 参数使用了 @Encrypt 注解，所以类型必须是 String 类型，而你使用了 ${typeName.simpleName} 类型"
		}
		val encryptType = annotation.getClassName("encryptType")?.simpleName ?: "SHA256"
		val hexType = annotation.getClassName("hexType")?.simpleName ?: "Lower"
		val layer = annotation.getValue<Int>("layer") ?: 1
		check(layer in 1 .. 10) {
			""
		}
		return EncryptInfo(encryptType, hexType, layer)
	}
}