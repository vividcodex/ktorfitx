package cn.vividcode.multiplatform.ktorfit.ksp.visitor.resolver

import cn.vividcode.multiplatform.ktorfit.annotation.Encrypt
import cn.vividcode.multiplatform.ktorfit.annotation.Form
import cn.vividcode.multiplatform.ktorfit.annotation.Path
import cn.vividcode.multiplatform.ktorfit.annotation.Query
import cn.vividcode.multiplatform.ktorfit.ksp.expends.getAnnotationByType
import cn.vividcode.multiplatform.ktorfit.ksp.model.EncryptInfo
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.FormModel
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.PathModel
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.QueryModel
import cn.vividcode.multiplatform.ktorfit.ksp.model.model.ValueParameterModel
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/3 14:30
 *
 * 文件介绍：EncryptValueParameterModelsResolver
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