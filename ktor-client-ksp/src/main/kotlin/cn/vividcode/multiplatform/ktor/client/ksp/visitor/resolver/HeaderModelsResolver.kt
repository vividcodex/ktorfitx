package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.annotation.Encrypt
import cn.vividcode.multiplatform.ktor.client.annotation.Header
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.EncryptInfo
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.HeaderModel
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/9 下午10:12
 *
 * 介绍：HeaderModelsResolver
 */
@Suppress("unused")
internal data object HeaderModelsResolver : ValueParameterModelResolver<HeaderModel> {
	
	private val encryptClassName by lazy { arrayOf(String::class.asClassName(), ByteArray::class.asClassName()) }
	
	override fun KSFunctionDeclaration.resolve(): List<HeaderModel> {
		return this.parameters.mapNotNull {
			val header = it.getAnnotationByType(Header::class) ?: return@mapNotNull null
			val varName = it.name!!.asString()
			val name = header.name.ifBlank { varName.replaceFirstChar { it.uppercaseChar() } }
			val encryptInfo = it.getAnnotationByType(Encrypt::class)?.let {
				EncryptInfo(it.encryptType, it.layer)
			}
			val className = (it.type.resolve().declaration as KSClassDeclaration).toClassName()
			check(encryptInfo == null || className in encryptClassName) {
				"${className.simpleName} 不允许使用 @Encrypt 注解"
			}
			HeaderModel(name, varName, encryptInfo)
		}
	}
}