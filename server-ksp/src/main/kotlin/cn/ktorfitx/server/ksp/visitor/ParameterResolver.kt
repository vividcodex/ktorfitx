package cn.ktorfitx.server.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.check.ktorfitxError
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.model.*
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object ParameterResolver {
	
	fun KSFunctionDeclaration.getVarNames(): List<String> {
		return this.parameters.map { parameter ->
			val count = ClassNames.parameterAnnotations.count { parameter.hasAnnotation(it) }
			parameter.compileCheck(count > 0) {
				val annotations = ClassNames.parameterAnnotations.joinToString { "@${it.simpleName}" }
				"${simpleName.asString()} 函数的 ${parameter.name!!.asString()} 参数必须使用 $annotations 注解中的一个"
			}
			parameter.compileCheck(count == 1) {
				val annotations = ClassNames.parameterAnnotations.joinToString { "@${it.simpleName}" }
				"${simpleName.asString()} 函数的 ${parameter.name!!.asString()} 参数只允许使用 $annotations 注解中的一个"
			}
			parameter.name!!.asString()
		}
	}
	
	fun KSFunctionDeclaration.getPrincipalModels(): List<PrincipalModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Principal) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
			}
			val provider = annotation.getValueOrNull<String>("provider")?.takeIf { it.isNotBlank() }
			PrincipalModel(varName, typeName, isNullable, provider)
		}
	}
	
	fun KSFunctionDeclaration.getQueryModels(): List<QueryModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Query) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
				parameter.compileCheck(typeName == ClassNames.String) {
					"${simpleName.asString()} 函数的 ${parameter.name!!.asString()} 参数可空类型只允许 String?"
				}
			}
			QueryModel(name, varName, typeName, isNullable)
		}
	}
	
	fun KSFunctionDeclaration.getPathModels(path: String): List<PathModel> {
		val pathParams = extractPathParams(path)
		val residuePathParams = pathParams.toMutableSet()
		val pathModels = this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Path) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			parameter.compileCheck(name in pathParams) {
				"${simpleName.asString()} 函数的 ${parameter.name!!.asString()} 参数未在 url 中找到"
			}
			parameter.compileCheck(name in residuePathParams) {
				"${simpleName.asString()} 函数的 ${parameter.name!!.asString()} 参数重复解析 path 参数"
			}
			residuePathParams -= name
			
			val typeName = parameter.type.toTypeName()
			parameter.compileCheck(!typeName.isNullable) {
				"${simpleName.asString()} 函数的 ${parameter.name!!.asString()} 参数不允许可空"
			}
			PathModel(name, varName, typeName)
		}
		this.compileCheck(residuePathParams.isEmpty()) {
			"${simpleName.asString()} 函数未解析 ${residuePathParams.joinToString { "\"$it\"" }} Path 参数"
		}
		return pathModels
	}
	
	private fun extractPathParams(path: String): Set<String> {
		val regex = "\\{([^}]+)}".toRegex()
		val matches = regex.findAll(path)
		val params = mutableSetOf<String>()
		for (match in matches) {
			params += match.groupValues[1]
		}
		return params
	}
	
	fun KSFunctionDeclaration.getRequestBody(
		routeModel: RouteModel
	): RequestBody? {
		val classNames = mapOf(
			BodyModel::class to ClassNames.Body,
			FieldModels::class to ClassNames.Field,
			PartModels::class to arrayOf(ClassNames.PartForm, ClassNames.PartFile, ClassNames.PartBinary, ClassNames.PartBinaryChannel)
		)
		val modelKClasses = classNames.mapNotNull { entity ->
			val exists = this.parameters.any { parameter ->
				val value = entity.value
				when (value) {
					is ClassName -> parameter.hasAnnotation(value)
					is Array<*> -> value.any { parameter.hasAnnotation(it as ClassName) }
					else -> error("不支持的类型")
				}
			}
			if (exists) entity.key else null
		}
		if (modelKClasses.isEmpty()) return null
		this.compileCheck(
			routeModel is HttpRequestModel &&
				routeModel.className in arrayOf(ClassNames.POST, ClassNames.PUT, ClassNames.DELETE, ClassNames.PATCH, ClassNames.OPTIONS)
		) {
			"${simpleName.asString()} 函数的参数中不允许使用 @Body, @Field, @PartForm, @PartFile, @PartBinary, @PartBinaryChannel 注解，因为请求类型必须是 @POST, @PUT, @DELETE, @PATCH, @OPTIONS 才能使用"
		}
		this.compileCheck(modelKClasses.size == 1) {
			"${simpleName.asString()} 函数参数不允许同时使用 @Body, @Field 或 @PartForm, @PartFile, @PartBinary, @PartBinaryChannel 注解"
		}
		val modelKClass = modelKClasses.single()
		return when (modelKClass) {
			BodyModel::class -> this.getBodyModel()
			FieldModels::class -> this.getFieldModels()
			PartModels::class -> this.getPartModels()
			else -> error("不支持的类型 ${modelKClass.simpleName}")
		}
	}
	
	private fun KSFunctionDeclaration.getBodyModel(): BodyModel {
		val filters = this.parameters.filter { it.hasAnnotation(ClassNames.Body) }
		this.compileCheck(filters.size == 1) {
			"${simpleName.asString()} 函数参数中不允许使用多个 @Body"
		}
		val body = filters.single()
		val varName = body.name!!.asString()
		var typeName = body.type.toTypeName()
		val isNullable = typeName.isNullable
		if (isNullable) {
			typeName = typeName.copy(nullable = false)
		}
		return BodyModel(varName, typeName, isNullable)
	}
	
	private fun KSFunctionDeclaration.getFieldModels(): FieldModels {
		val parameters = this.parameters.filter { it.hasAnnotation(ClassNames.Field) }
		val fieldModels = parameters.map { parameter ->
			val varName = parameter.name!!.asString()
			val annotation = parameter.getKSAnnotationByType(ClassNames.Field)!!
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
				parameter.compileCheck(typeName == ClassNames.String) {
					"${simpleName.asString()} 函数的 $varName 参数可空类型只允许 String?"
				}
			}
			FieldModel(name, varName, typeName, isNullable)
		}
		return FieldModels(fieldModels)
	}
	
	private fun KSFunctionDeclaration.getPartModels(): PartModels {
		val configs = listOf(
			PartModelConfig(
				annotation = ClassNames.PartForm,
				classNames = listOf(
					ClassNames.FormItem,
					ClassNames.String
				),
				errorMessage = { "${simpleName.asString()} 函数的 ${it.name!!.asString()} 参数只允许使用 String 和 PartData.FormItem 类型" }
			),
			PartModelConfig(
				annotation = ClassNames.PartFile,
				classNames = listOf(
					ClassNames.FileItem,
					ClassNames.ByteArray
				),
				errorMessage = { "${simpleName.asString()} 函数的 ${it.name!!.asString()} 参数只允许使用 ByteArray 和 PartData.FileItem 类型" }
			),
			PartModelConfig(
				annotation = ClassNames.PartBinary,
				classNames = listOf(
					ClassNames.BinaryItem,
					ClassNames.ByteArray
				),
				errorMessage = { "${simpleName.asString()} 函数的 ${it.name!!.asString()} 参数只允许使用 ByteArray 和 PartData.BinaryItem 类型" }
			),
			PartModelConfig(
				annotation = ClassNames.PartBinaryChannel,
				classNames = listOf(
					ClassNames.BinaryChannelItem
				),
				errorMessage = { "${simpleName.asString()} 函数的 ${it.name!!.asString()} 参数只允许使用 PartData.BinaryChannelItem 类型" }
			),
		)
		val allPartModels = configs.flatMap { getPartModels(it) }
		return PartModels(allPartModels)
	}
	
	private fun KSFunctionDeclaration.getPartModels(
		config: PartModelConfig
	): List<PartModel> {
		val partForms = this.parameters.filter { it.hasAnnotation(config.annotation) }
		return partForms.map { parameter ->
			val varName = parameter.name!!.asString()
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
			}
			val annotation = parameter.getKSAnnotationByType(config.annotation)!!
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			config.classNames.forEach { className ->
				if (typeName == className) {
					val isPartData = className in ClassNames.partDatas
					return@map PartModel(name, varName, config.annotation, className, isNullable, isPartData)
				}
			}
			parameter.ktorfitxError {
				config.errorMessage(parameter)
			}
		}
	}
	
	fun KSFunctionDeclaration.getHeaderModels(): List<HeaderModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Header) ?: return@mapNotNull null
			val varName = parameter.name!!.asString()
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName.camelToHeaderCase()
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
			}
			parameter.compileCheck(typeName == ClassNames.String) {
				"${simpleName.asString()} 函数的 $varName 参数只允许使用 String 类型"
			}
			HeaderModel(name, varName, isNullable)
		}
	}
	
	fun KSFunctionDeclaration.getCookieModels(): List<CookieModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Cookie) ?: return@mapNotNull null
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
			}
			val varName = parameter.name!!.asString()
			parameter.compileCheck(typeName == ClassNames.String) {
				"${simpleName.asString()} 函数的 $varName 参数只允许使用 String 类型"
			}
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			val encoding = annotation.getClassNameOrNull("encoding")?.simpleName?.let { simpleName ->
				when (simpleName) {
					ClassNames.CookieEncodingRaw.simpleName -> ClassNames.CookieEncodingRaw
					ClassNames.CookieEncodingDQuotes.simpleName -> ClassNames.CookieEncodingDQuotes
					ClassNames.CookieEncodingURIEncoding.simpleName -> ClassNames.CookieEncodingURIEncoding
					ClassNames.CookieEncodingBase64Encoding.simpleName -> ClassNames.CookieEncodingBase64Encoding
					else -> error("不支持的类型")
				}
			} ?: ClassNames.CookieEncodingURIEncoding
			CookieModel(name, varName, isNullable, encoding)
		}
	}
	
	fun KSFunctionDeclaration.getAttributeModels(): List<AttributeModel> {
		return this.parameters.mapNotNull { parameter ->
			val annotation = parameter.getKSAnnotationByType(ClassNames.Attribute) ?: return@mapNotNull null
			var typeName = parameter.type.toTypeName()
			val isNullable = typeName.isNullable
			if (isNullable) {
				typeName = typeName.copy(nullable = false)
			}
			val varName = parameter.name!!.asString()
			val name = annotation.getValueOrNull<String>("name")?.takeIf { it.isNotBlank() } ?: varName
			AttributeModel(name, varName, typeName, isNullable)
		}
	}
}

private data class PartModelConfig(
	val annotation: ClassName,
	val classNames: List<ClassName>,
	val errorMessage: (KSValueParameter) -> String
)