package cn.vividcode.multiplatform.ktor.client.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktor.client.annotation.Encrypt
import cn.vividcode.multiplatform.ktor.client.annotation.Form
import cn.vividcode.multiplatform.ktor.client.annotation.Path
import cn.vividcode.multiplatform.ktor.client.annotation.Query
import cn.vividcode.multiplatform.ktor.client.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktor.client.ksp.model.EncryptInfo
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.FormModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.PathModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.QueryModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.model.ValueParameterModel
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
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
@Suppress("unused")
internal data object EncryptValueParameterModelsResolver : ValueParameterModelResolver<ValueParameterModel> {
	
	private val encryptClassName by lazy { arrayOf(String::class.asClassName(), ByteArray::class.asClassName()) }
	
	override fun KSFunctionDeclaration.resolve(): List<ValueParameterModel> {
		return buildList {
			this += getValueParameterModels(Query::name, ::QueryModel)
			this += getValueParameterModels(Form::name, ::FormModel)
			this += getValueParameterModels(Path::name, ::PathModel)
		}
	}
	
	private inline fun <reified A : Annotation, M : ValueParameterModel> KSFunctionDeclaration.getValueParameterModels(
		getName: (A) -> String,
		newModel: (name: String, varName: String, encryptInfo: EncryptInfo?) -> M
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
			newModel(name, varName, encryptInfo)
		}
	}
}