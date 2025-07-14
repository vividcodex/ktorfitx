package cn.ktorfitx.server.ksp.visitor

import cn.ktorfitx.common.ksp.util.check.compileCheck
import cn.ktorfitx.common.ksp.util.check.compileError
import cn.ktorfitx.common.ksp.util.expends.*
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.model.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class RouteVisitor : KSEmptyVisitor<Unit, FunModel>() {
	
	override fun visitFunctionDeclaration(
		function: KSFunctionDeclaration,
		data: Unit
	): FunModel {
		function.checkReturnType()
		val routeModel = function.getRouteModel()
		return FunModel(
			function.simpleName.asString(),
			function.getCanonicalName(),
			function.extensionReceiver != null,
			function.getGroupName(),
			function.getAuthenticationModel(),
			routeModel,
			function.getVarNames(),
			function.getPrincipalModels(),
			function.getQueryModels(),
			function.getRequestBody(routeModel)
		)
	}
	
	private fun KSFunctionDeclaration.getCanonicalName(): String {
		val parent = this.parentDeclaration
		return when (parent) {
			is KSClassDeclaration -> parent.toClassName().canonicalName
			else -> this.packageName.asString()
		}
	}
	
	private fun KSFunctionDeclaration.getGroupName(): String? {
		val annotation = this.getKSAnnotationByType(ClassNames.Group) ?: return null
		return annotation.getValue("name")
	}
	
	private fun KSFunctionDeclaration.checkReturnType() {
		val returnType = this.returnType!!.resolve()
		returnType.declaration.compileCheck(!returnType.isMarkedNullable) {
			"${this.simpleName} 函数返回类型不允许为可空类型"
		}
		val typeName = returnType.toTypeName()
		val validTypeName = typeName is ClassName || typeName is ParameterizedTypeName
		returnType.declaration.compileCheck(validTypeName) {
			"${this.simpleName} 函数返回类型必须是明确的类"
		}
	}
	
	private fun KSFunctionDeclaration.getAuthenticationModel(): AuthenticationModel? {
		val annotation = this.getKSAnnotationByType(ClassNames.Authentication) ?: return null
		val configurations = annotation.getValues<String>("configurations")!!
		val strategy = annotation.getClassName("strategy")!!
		return AuthenticationModel(configurations, strategy)
	}
	
	private fun KSFunctionDeclaration.getRouteModel(): RouteModel {
		val dataList = ClassNames.routes.mapNotNull {
			this.getKSAnnotationByType(it)?.let(it::to)
		}
		this.compileCheck(dataList.size == 1) {
			"${this.simpleName} 不允许同时添加多个请求类型"
		}
		val data = dataList.first()
		val className = data.first
		val annotation = data.second
		val path = annotation.getValue<String>("path").removePrefix("/").removeSuffix("/")
		val isExtension = this.extensionReceiver != null
		return when (className) {
			ClassNames.WebSocket -> {
				val protocol = annotation.getValueOrNull("protocol") ?: ""
				if (isExtension) {
					val valid = this.isExtension(ClassNames.DefaultWebSocketServerSession)
					this.compileCheck(valid) {
						"${this.simpleName} 是扩展函数，但仅允许扩展 DefaultWebSocketServerSession"
					}
				}
				WebSocketModel(path, protocol)
			}
			
			ClassNames.WebSocketRaw -> {
				val protocol = annotation.getValueOrNull("protocol") ?: ""
				val negotiateExtensions = annotation.getValueOrNull("negotiateExtensions") ?: false
				if (isExtension) {
					val valid = this.isExtension(ClassNames.WebSocketServerSession)
					this.compileCheck(valid) {
						"${this.simpleName} 是扩展函数，但仅允许扩展 WebSocketServerSession"
					}
				}
				WebSocketRawModel(path, protocol, negotiateExtensions)
			}
			
			else -> {
				if (isExtension) {
					val valid = this.isExtension(ClassNames.RoutingContext)
					this.compileCheck(valid) {
						"${this.simpleName} 是扩展函数，但仅允许扩展 RoutingContext"
					}
				}
				HttpRequestModel(path, className)
			}
		}
	}
	
	private fun KSFunctionDeclaration.getVarNames(): List<String> {
		return this.parameters.mapNotNull { parameter ->
			parameter.name?.asString()?.takeIf { it.isNotBlank() }
		}
	}
	
	private fun KSFunctionDeclaration.getPrincipalModels(): List<PrincipalModel> {
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
	
	private fun KSFunctionDeclaration.getRequestBody(
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
			parameter.compileError {
				config.errorMessage(parameter)
			}
		}
	}
	
	private fun KSFunctionDeclaration.getQueryModels(): List<QueryModel> {
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
	
	override fun defaultHandler(node: KSNode, data: Unit): FunModel = error("Not Implemented")
}

private data class PartModelConfig(
	val annotation: ClassName,
	val classNames: List<ClassName>,
	val errorMessage: (KSValueParameter) -> String
)