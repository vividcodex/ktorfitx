package cn.ktorfitx.server.ksp.model

import cn.ktorfitx.server.ksp.constants.ClassNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

internal open class RouteModel(
	open val functionClassName: ClassName,
	open val requestMethod: String,
	open val path: String,
	open val returnTypeName: TypeName
)

internal fun ClassName.toRequestMethod(): String {
	return when (this) {
		ClassNames.GET -> "get"
		ClassNames.POST -> "post"
		ClassNames.PUT -> "put"
		ClassNames.DELETE -> "delete"
		ClassNames.PATCH -> "patch"
		ClassNames.HEAD -> "head"
		ClassNames.OPTIONS -> "options"
		else -> error("Unsupported class: $this")
	}
}