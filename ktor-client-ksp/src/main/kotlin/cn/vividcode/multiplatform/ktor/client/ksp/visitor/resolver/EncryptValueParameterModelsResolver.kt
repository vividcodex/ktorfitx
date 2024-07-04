package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.api.annotation.*
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/3 下午2:30
 *
 * 介绍：EncryptValueParameterModelsResolver
 */
internal data object EncryptValueParameterModelsResolver : ValueParameterModelResolver<ValueParameterModel> {
	
	private val encryptClassName = arrayOf(String::class.asClassName(), ByteArray::class.asClassName())
	
	override fun KSFunctionDeclaration.getValueParameterModels(): List<ValueParameterModel> {
		return buildList {
			this += getValueParameterModels(Query::name, ::QueryModel)
			this += getValueParameterModels(Header::name, ::HeaderModel)
			this += getValueParameterModels(Form::name, ::FormModel)
			this += getValueParameterModels(Path::name, ::PathModel)
		}
	}
	
	private inline fun <reified A : Annotation, M : ValueParameterModel> KSFunctionDeclaration.getValueParameterModels(
		getName: (A) -> String,
		newModel: (name: String, varName: String, encryptInfo: EncryptInfo?, className: ClassName) -> M
	): List<M> {
		return this.parameters.mapNotNull {
			val annotation = it.getAnnotationByType(A::class) ?: return@mapNotNull null
			val varName = it.name!!.asString()
			val name = getName(annotation).ifBlank { varName }
			val encryptInfo = it.getAnnotationByType(Encrypt::class)?.let {
				EncryptInfo(it.encryptType, it.layer)
			}
			val className = (it.type.resolve().declaration as KSClassDeclaration).toClassName()
			check(encryptInfo == null || className in encryptClassName) {
				"${className.simpleName} 不允许使用 @Encrypt 注解"
			}
			newModel(name, varName, encryptInfo, className)
		}
	}
}